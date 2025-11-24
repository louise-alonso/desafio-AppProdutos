package br.com.louise.AppProdutos.repository;

import br.com.louise.AppProdutos.model.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, Long> {

    boolean existsByCode(String code);

    Optional<CouponEntity> findByCode(String code);
}