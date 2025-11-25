package br.com.louise.AppProdutos.service;

import br.com.louise.AppProdutos.dto.user.DTOUserRequest;
import br.com.louise.AppProdutos.dto.user.DTOUserResponse;

import java.util.List;

public interface UserService {

    DTOUserResponse createUser(DTOUserRequest request, String currentUserRole);
    DTOUserResponse createFirstAdmin(DTOUserRequest request);

    String getUserRole(String email);

    List<DTOUserResponse> readUsers();

    void deleteUser(String id);

    DTOUserResponse updateUser(String userId, DTOUserRequest request);
}
