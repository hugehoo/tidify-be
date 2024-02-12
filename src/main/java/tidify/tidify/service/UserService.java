package tidify.tidify.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tidify.tidify.domain.SocialType;
import tidify.tidify.domain.User;
import tidify.tidify.dto.UserDto;
import tidify.tidify.oauth.SocialLoginFactory;
import tidify.tidify.repository.UserRepository;
import tidify.tidify.security.JwtTokenProvider;
import tidify.tidify.security.Token;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final SocialLoginFactory socialLoginFactory;

    private final UserRepository userRepository;

    @Transactional
    public Token getAuthenticate(String idToken, SocialType type) {
        Token token = getToken(idToken, type);
        // saveTokenInRedis(token);
        saveOrUpdateUser(token);
        return token;
    }

    private Token getToken(String idToken, SocialType type) {
        String email = socialLoginFactory.getEmail(type).apply(idToken);
        return jwtTokenProvider.createToken(email, type);
    }


    private void saveOrUpdateUser(Token token) {
        String userEmail = jwtTokenProvider.getUserPk(token.accessToken(), false);
        userRepository.findUserByEmailAndDelFalse(userEmail)
            .ifPresentOrElse(existUser -> updateTokens(existUser, token),
                () -> saveUser(token));
    }

    private void saveUser(Token token) {
        String email = token.key();
        UserDto userDto = UserDto.of(email, passwordEncoder.encode(email), token);
        User user = socialLoginFactory.getSocialType(token.type())
            .apply(userDto);

        userRepository.save(user);
    }

    private void updateTokens(User user, Token token) {
        user.setAccessToken(token.accessToken());
        user.setRefreshToken(token.refreshToken());
    }

    // 이거 탈취당한 토큰으로 그냥 찌르기만 하면 걍 삭제되는데, 다른 대책을 세워야할듯
    public void userWithdraw(User user) {
        // Todo user 연관된 folder, bookmark 모두 삭제
        // accountRepository.deleteUserRelateInfo(user);
        userRepository.delete(user);
    }
}
