package tsukoyachi.camelshop.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tsukoyachi.camelshop.common.entity.Order;

public interface OrderRepository extends JpaRepository<Order,String> {
}
