package tsukoyachi.camelshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tsukoyachi.camelshop.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order,String> {
}
