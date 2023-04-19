package tidify.tidify.redis;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface RedisTokenRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findById(String userEmail);

    @Override
    void deleteById(String userPk);
}