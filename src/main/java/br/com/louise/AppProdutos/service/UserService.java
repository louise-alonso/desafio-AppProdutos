package br.com.louise.AppProdutos.service;

import br.com.louise.AppProdutos.dto.DTOUserRequest;
import br.com.louise.AppProdutos.dto.DTOUserResponse;

import java.util.List;

public interface UserService {

    DTOUserResponse createUser(DTOUserRequest request);

    String getUserRole(String email);

    List<DTOUserResponse> readUsers();

    void deleteUser(String id);

}
