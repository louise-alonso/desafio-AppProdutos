package br.com.louise.AppProdutos.service;

import br.com.louise.AppProdutos.dto.UserRequest;
import br.com.louise.AppProdutos.dto.UserResponse;

import java.util.List;

public interface UserService {


    UserResponse createUser(UserRequest request);

    String getUserRole(String email);

    List<UserResponse> readUsers();

    void deleteUser(String id);


}
