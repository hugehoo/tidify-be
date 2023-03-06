package tidify.tidify.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tidify.tidify.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmailAndDelFalse(String email);

    boolean existsByRefreshToken(String refreshToken);

    User findUserByRefreshToken(String refreshToken);

}

