package tidify.tidify.service;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tidify.tidify.common.kakao.KAKAOFeign;
import tidify.tidify.common.kakao.KAKAOLoginTokenInfo;
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

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String url;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String grantType;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    private final PasswordEncoder passwordEncoder;

    private final KAKAOFeign kakaoFeign;
    private final UserRepository userRepository;


    // @Transactional
    // public Token getAppleAccessTokenFeign(String authorizationCode) {
    //     // appleFeign
    //
    // }

    @Transactional
    public Token getKakaoAccessTokenFeign(String authorizationCode) {
        KAKAOLoginTokenInfo tokenInfo = kakaoFeign.getAccessToken(grantType, clientId, redirectUri, authorizationCode);
        log.info(tokenInfo.toString());
        // access_token 으로 유저 정보를 kakao 에서 가져옴
        User kakaoUser = createKakaoUser(tokenInfo); // jwt 로는 유저 이메일만 가져온다. 아님 유저 다 가져와야함
        Token token = jwtTokenProvider.createToken(kakaoUser.getUsername());
        // kakaoUser.setAccessToken(token.getAccessToken());
        kakaoUser.setRefreshToken(token.getRefreshToken());

        Optional<User> user = userRepository.findUserByEmailAndDelFalse(kakaoUser.getEmail());
        if (user.isEmpty()) {
            userRepository.save(kakaoUser);
        }
        return token;
    }

    public User createKakaoUser(KAKAOLoginTokenInfo kakaoToken) {
        try {
            ResponseEntity<String> response2 = getKakaoUserInformation(kakaoToken);
            ObjectMapper mapper = new ObjectMapper();
            KakaoInfoResponse response = mapper.readValue(response2.getBody(), KakaoInfoResponse.class);
            String password = passwordEncoder.encode(response.getId() + response.getAccount().getEmail());
            return new User(response, password, kakaoToken);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ResponseEntity<String> getKakaoUserInformation(KAKAOLoginTokenInfo kakaoToken) {
        RestTemplate rt2 = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", kakaoToken.getAccess_token());
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        HttpEntity<MultiValueMap<String, String>> profileRequest = new HttpEntity<>(headers);
        return rt2.exchange(url, HttpMethod.POST, profileRequest, String.class);
    }
}
