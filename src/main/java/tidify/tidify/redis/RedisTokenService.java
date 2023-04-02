package tidify.tidify.redis;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tidify.tidify.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final RedisTokenRepository redisTokenRepository;

    private final JwtTokenProvider jwtTokenProvider;

    public boolean existsRefreshToken(String refreshToken) {
        return redisTokenRepository.existsById(refreshToken);
    }

    public void saveTokenInRedis(String refreshToken) {
        String userEmail = jwtTokenProvider.getUserPk(refreshToken, true);
        redisTokenRepository.save(RefreshToken.of(refreshToken, userEmail));
    }

    public String getRedisValue(String refreshToken) {
        return redisTokenRepository.findRefreshTokenByRefreshToken(refreshToken)
            .orElseThrow()
            .getUserEmail();
    }
}
