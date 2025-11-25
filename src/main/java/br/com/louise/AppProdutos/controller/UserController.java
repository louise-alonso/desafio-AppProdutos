package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.user.DTOUserRequest;
import br.com.louise.AppProdutos.dto.user.DTOUserResponse;
import br.com.louise.AppProdutos.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // ← ADICIONAR ESTE IMPORT
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "01. Autenticação e Usuários", description = "Gestão de contas e acesso")
public class UserController {

    private final UserService userService;

    // ENDPOINT PÚBLICO - APENAS para primeiro admin
    @PostMapping("/register-first")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar primeiro administrador", description = "Endpoint público APENAS para o primeiro ADMIN do sistema.")
    public DTOUserResponse registerFirstAdmin(@RequestBody @Valid DTOUserRequest request) {
        return userService.createFirstAdmin(request);
    }

    // ENDPOINT PROTEGIDO - Para criar outros usuários
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Criar utilizador", description = "Cria um novo usuário. ADMIN pode criar qualquer role, outros apenas CUSTOMER.")
    public DTOUserResponse registerUser(@RequestBody @Valid DTOUserRequest request, Authentication authentication) {
        String currentUserRole = getCurrentUserRole(authentication);
        return userService.createUser(request, currentUserRole);
    }

    private String getCurrentUserRole(Authentication authentication) {
        return authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar utilizadores", description = "Lista todos os utilizadores cadastrados. Exclusivo para ADMIN.")
    public List<DTOUserResponse> readUsers() {
        return userService.readUsers();
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir utilizador", description = "Remove um utilizador do sistema pelo ID.")
    public void deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar utilizador", description = "Altera dados cadastrais de um utilizador.")
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