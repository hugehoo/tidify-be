package tidify.tidify.service;

import java.util.Optional;

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

    @Transactional
    public Token getJWTTokens(String idToken, SocialType type) {
        String email = socialLoginFactory.getEmail(type).apply(idToken);
        Token token = jwtTokenProvider.createToken(email);
        createUser(email, token, type);
        return token;
    }

    private void createUser(String userEmail, Token token, SocialType type) {
        String password = passwordEncoder.encode(userEmail);
        UserDto userDto = new UserDto(userEmail, password, token.getAccessToken(), token.getRefreshToken());
        userRepository.findUserByEmailAndDelFalse(userEmail)
            .ifPresentOrElse(
                user -> updateTokens(token, user),
                () -> saveUser(type, userDto)
            );
    }

    private void saveUser(SocialType type, UserDto userDto) {
        User newUser = socialLoginFactory.getSocialType(type).apply(userDto);
        userRepository.save(newUser);
    }

    private void updateTokens(Token token, User user) {
        user.setAccessToken(token.getAccessToken());
        user.setRefreshToken(token.getRefreshToken());
    }
}
