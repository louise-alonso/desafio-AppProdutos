package br.com.louise.AppProdutos.repository;

import br.com.louise.AppProdutos.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUserId(String userId);
    List<UserEntity> findByRole(String role);

    @Transactional
    void deleteByUserId(String userId);
}