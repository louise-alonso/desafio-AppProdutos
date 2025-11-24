package br.com.louise.AppProdutos.repository;

import br.com.louise.AppProdutos.model.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByUserEmail(String email);
}