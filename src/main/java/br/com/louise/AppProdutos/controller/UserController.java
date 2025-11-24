package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.user.DTOUserRequest;
import br.com.louise.AppProdutos.dto.user.DTOUserResponse;

import br.com.louise.AppProdutos.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/admin") // ou sua rota base
@RequiredArgsConstructor
@Tag(name = "01. Autenticação e Usuários")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public DTOUserResponse registerUser(@RequestBody @Valid DTOUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DTOUserResponse> readUsers() {
        return userService.readUsers();
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DTOUserResponse updateUsers(@PathVariable String id, @RequestBody DTOUserRequest request) {
        try {
            return userService.updateUser(id, request);
        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falha na atualização: " + e.getMessage());
        }
    }
}