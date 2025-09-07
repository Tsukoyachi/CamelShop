package tsukoyachi.camelshop.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tsukoyachi.camelshop.common.entity.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
}
