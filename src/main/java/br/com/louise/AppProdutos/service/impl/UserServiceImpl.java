package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.DTOUserRequest;
import br.com.louise.AppProdutos.dto.DTOUserResponse;
import br.com.louise.AppProdutos.model.UserEntity;
import br.com.louise.AppProdutos.repository.UserRepository;
import br.com.louise.AppProdutos.service.UserService;
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
    public DTOUserResponse createUser(DTOUserRequest request) {
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


    private UserEntity convertToEntity(DTOUserRequest request) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole().toUpperCase())
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
        UserEntity existingUser = userRepository.findByUserId(id)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
                userRepository.delete(existingUser);


    }

    @Override
    public DTOUserResponse updateUser(String userId, DTOUserRequest request) {
        // 1. Busca o usuário existente pelo userId (o ID único)
        UserEntity existingUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for update: " + userId));

        // 2. Validação: Checar se o novo e-mail (se alterado) já existe em outro usuário
        if (!existingUser.getEmail().equalsIgnoreCase(request.getEmail()) && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists for another user.");
        }

        // 3. Atualiza campos
        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail());
        existingUser.setRole(request.getRole().toUpperCase()); // Novo Role (ADMIN, SELLER, CUSTOMER)

        // 4. Atualiza a Senha (Se for fornecida no request)
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // 5. Salva e retorna
        existingUser = userRepository.save(existingUser);
        return convertToResponse(existingUser);
    }
}
