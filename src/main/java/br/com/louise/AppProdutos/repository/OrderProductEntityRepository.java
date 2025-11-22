package br.com.louise.AppProdutos.repository;

import br.com.louise.AppProdutos.model.OrderProductEntity;
import org.springframework.data.repository.CrudRepository;

public interface OrderProductEntityRepository extends CrudRepository<OrderProductEntity, Long> {

}