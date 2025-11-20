package br.com.louise.AppProdutos.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.louise.AppProdutos.model.UserEntity; 


public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUserId(String userId);
    
}