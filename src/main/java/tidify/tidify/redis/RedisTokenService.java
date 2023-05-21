package tidify.tidify.redis;

import static tidify.tidify.common.Constants.X_AUTH_TOKEN;
import static tidify.tidify.common.Constants.REFRESH_TOKEN;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tidify.tidify.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final RedisTokenRepository redisTokenRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Map<String, String> createBothTokens(String refreshToken) {
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            return logoutProcess(refreshToken);
        }

        Map<String, String> map = new HashMap<>();
        getToken(refreshToken).ifPresentOrElse(
            token -> reIssueBothTokens(token.getUserEmail(), map),
            () -> logoutProcess(refreshToken)
        );
//
        String userPk = jwtTokenProvider.getUserPk(refreshToken, true);
        reIssueBothTokens(userPk, map);

        //
        return map;
    }

    private Optional<RefreshToken> getToken(String refreshToken) {
        // 만약 여기서 예외터지면 어캄?
        String userPk = jwtTokenProvider.getUserPk(refreshToken, true);
        return redisTokenRepository.findById(userPk)
            .filter(token -> compareRefreshTokens(refreshToken, token));
    }

    private boolean compareRefreshTokens(String refreshToken, RefreshToken token) {
        return token.getRefreshToken().equals(refreshToken);
    }

    private Map<String, String> logoutProcess(String refreshToken) {
        logout(refreshToken);
        return Collections.emptyMap();
    }

    private void reIssueBothTokens(String userPk, Map<String, String> map) {
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userPk);
        String newAccessToken = jwtTokenProvider.createAccessToken(userPk);
        map.put(X_AUTH_TOKEN, newAccessToken);
        map.put(REFRESH_TOKEN, newRefreshToken);

        redisTokenRepository.save(RefreshToken.of(userPk, newRefreshToken));
    }

    private void logout(String refreshToken) {
        String userPk = jwtTokenProvider.getUserPk(refreshToken, true);
        redisTokenRepository.deleteById(userPk);
    }

}
