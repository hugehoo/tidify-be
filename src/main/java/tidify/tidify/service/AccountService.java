package tidify.tidify.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tidify.tidify.security.JwtTokenProvider;
import tidify.tidify.security.Token;
import tidify.tidify.domain.SocialType;
import tidify.tidify.repository.UserRepository;
import tidify.tidify.domain.User;
import tidify.tidify.oauth.SocialLoginFactory;
import tidify.tidify.dto.UserDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final SocialLoginFactory socialLoginFactory;
    private final UserRepository userRepository;

    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public Token getJWTTokens(String idToken, SocialType type) {
        String email = socialLoginFactory.getEmail(type).apply(idToken);
        Token token = jwtTokenProvider.createToken(email);
        saveOrUpdateUser(email, token, type);
        return token;
    }

    private void saveOrUpdateUser(String userEmail, Token token, SocialType type) {
        userRepository.findUserByEmailAndDelFalse(userEmail)
            .ifPresentOrElse(existUser -> updateTokens(existUser, token),
                () -> saveUser(type, userEmail, token));
    }

    private void saveUser(SocialType type, String email, Token token) {
        UserDto userDto = UserDto.of(email, passwordEncoder.encode(email), token);
        User user = socialLoginFactory.getSocialType(type).apply(userDto);
        userRepository.save(user);
    }

    private void updateTokens(User user, Token token) {
        user.setAccessToken(token.getAccessToken());
        user.setRefreshToken(token.getRefreshToken());
    }

    private void setTokenRedis(Token token) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(token.getRefreshToken(), token.getAccessToken());
        ops.getAndPersist(token.getRefreshToken());
    }
}
