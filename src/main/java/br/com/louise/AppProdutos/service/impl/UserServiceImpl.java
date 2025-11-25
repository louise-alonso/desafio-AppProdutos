package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.user.DTOUserRequest;
import br.com.louise.AppProdutos.dto.user.DTOUserResponse;
import br.com.louise.AppProdutos.model.UserEntity;
import br.com.louise.AppProdutos.repository.UserRepository;
import br.com.louise.AppProdutos.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public DTOUserResponse createUser(DTOUserRequest request, String currentUserRole) {
        // VALIDAÇÃO DE SEGURANÇA: Apenas ADMIN pode criar outros ADMIN
        if ("ADMIN".equalsIgnoreCase(request.getRole()) && !"ADMIN".equals(currentUserRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Apenas administradores podem criar outros administradores");
        }

        UserEntity newUser = convertToEntity(request);
        newUser = userRepository.save(newUser);
        return convertToResponse(newUser);
    }

    @Transactional
    public DTOUserResponse createFirstAdmin(DTOUserRequest request) {
        // Força role ADMIN para primeiro usuário
        if (!userRepository.findByRole("ADMIN").isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Já existe um administrador no sistema");
        }

        request.setRole("ADMIN"); // Garante que seja ADMIN
        UserEntity newUser = convertToEntity(request);
        newUser = userRepository.save(newUser);
        return convertToResponse(newUser);
    }

    private DTOUserResponse convertToResponse(UserEntity newUser) {
        return DTOUserResponse.builder()
                .name(newUser.getName())
                .userId(newUser.getUserId())
                .email(newUser.getEmail())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .role(newUser.getRole())
                .build();
    }

    private String validateRole(String roleInput) {
        if (roleInput == null) return "CUSTOMER";

        String role = roleInput.toUpperCase();
        if (!role.equals("ADMIN") && !role.equals("SELLER") && !role.equals("CUSTOMER")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Role inválida. Opções permitidas: ADMIN, SELLER, CUSTOMER");
        }
        return role;
    }

    private UserEntity convertToEntity(DTOUserRequest request) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(validateRole(request.getRole()))
                .name(request.getName())
                .build();

    }


    @Override
    public String getUserRole(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found for the email:" +email));
        return existingUser.getRole();
    }



    @Override
    public List<DTOUserResponse> readUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> convertToResponse(user))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.findByUserId(id).isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }
        userRepository.deleteByUserId(id);
    }

    @Override
    public DTOUserResponse updateUser(String userId, DTOUserRequest request) {
        UserEntity existingUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for update: " + userId));

        if (!existingUser.getEmail().equalsIgnoreCase(request.getEmail()) && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists for another user.");
        }

        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail());
        existingUser.setRole(validateRole(request.getRole().toUpperCase()));

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        existingUser = userRepository.save(existingUser);
        return convertToResponse(existingUser);
    }

}
