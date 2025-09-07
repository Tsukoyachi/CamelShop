package tsukoyachi.camelshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tsukoyachi.camelshop.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
