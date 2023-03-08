package tidify.tidify.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tidify.tidify.common.kakao.KAKAOFeign;
import tidify.tidify.common.kakao.KAKAOLoginTokenInfo;
import tidify.tidify.common.kakao.KAKAOUserFeign;
import tidify.tidify.common.kakao.KakaoInfoResponse;
import tidify.tidify.common.security.JwtTokenProvider;
import tidify.tidify.common.security.Token;
import tidify.tidify.repository.UserRepository;
import tidify.tidify.domain.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String grantType;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    private final PasswordEncoder passwordEncoder;

    private final KAKAOFeign kakaoFeign;
    private final KAKAOUserFeign kakaoUserFeign;
    private final UserRepository userRepository;

    @Transactional
    public KAKAOLoginTokenInfo getAccessTokenFromKAKAO(String authorizationCode) {
        KAKAOLoginTokenInfo tokenInfo = kakaoFeign.getAccessToken(grantType, clientId, redirectUri, authorizationCode);
        log.info(tokenInfo.toString());
        return tokenInfo;
    }

    // 이 DTO 를 응답하면, ios 개발자들이 헤더에 박을 수 있나?
    // 여기 일이 많아. 토큰도 발급하고, 유저도 저장하고.
    // public Token createToken(KAKAOLoginTokenInfo tokenInfo) {
    @Transactional
    public Token createToken(String authorizationCode) {
        KAKAOLoginTokenInfo tokenInfo = getAccessTokenFromKAKAO(authorizationCode);
        User user = createKakaoUser(tokenInfo); // jwt 로는 유저 이메일만 가져온다. 아님 유저 다 가져와야함
        Token token = jwtTokenProvider.createToken(user.getUsername());
        saveOrPassUser(user, token);
        return token;
    }

    // 지금 회원가입과 로그인이 분리가 안된 상태.
    // 로그인으로 들어오지만, 회원가입을 하고 있다.
    private void saveOrPassUser(User user, Token token) {
        //
        // Optional<User> userOptional = userRepository.findUserByEmailAndDelFalse(user.getEmail());
        // if (userOptional.isEmpty()) {
        //     user.setRefreshToken(token.getRefreshToken());
        //     userRepository.save(user);
        // } else {
        //     User existUser = userOptional.get();
        //     existUser.setRefreshToken(token.getRefreshToken());
        // }
        //

        // user.setAccessToken(token.getAccessToken());
        Optional<User> findUser = userRepository.findUserByEmailAndDelFalse(user.getEmail());
        if (findUser.isEmpty()) {
            user.setRefreshToken(token.getRefreshToken());
            userRepository.save(user);
        } // findUser 가 존재할 때도 refreshToken 을 갱신해야 하지 않을까? 다시 로그인 했다는 건, 두 토큰이 모두 만료됐단 뜻이니까.
    }

    public User createKakaoUser(KAKAOLoginTokenInfo token) {
        KakaoInfoResponse userInfo = kakaoUserFeign.getUserInfo(token.getAccess_token());
        String password = passwordEncoder.encode(userInfo.getId() + userInfo.getAccount().getEmail());
        return new User(userInfo, password, token);
    }
}
