package tsukoyachi.camelshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tsukoyachi.camelshop.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
}
