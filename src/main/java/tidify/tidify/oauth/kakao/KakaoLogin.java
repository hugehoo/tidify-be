package tidify.tidify.oauth.kakao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tidify.tidify.oauth.SocialLogin;
import tidify.tidify.oauth.kakao.feign.KakaoTokenFeign;
import tidify.tidify.oauth.kakao.KakaoLoginTokenInfo;
import tidify.tidify.oauth.kakao.feign.KakaoUserFeign;
import tidify.tidify.domain.SocialType;
import tidify.tidify.domain.User;
import tidify.tidify.dto.UserDto;

@Component
@RequiredArgsConstructor
public class KakaoLogin implements SocialLogin {

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String grantType;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    private final KakaoTokenFeign kakaoTokenFeign;
    private final KakaoUserFeign kakaoUserFeign;

    @Override
    public SocialType getSocialType() {
        return SocialType.KAKAO;
    }

    @Override
    public User userTransaction(UserDto user) {
        return User.ofKakao(user.getEmail(), user.getPassword(), user.getAccessToken(), user.getRefreshToken());
    }

    @Override
    public String emailTransaction(String code) {
        KakaoLoginTokenInfo tokenInfo = kakaoTokenFeign.getAccessToken(grantType, clientId, redirectUri, code);
        return kakaoUserFeign.getUserInfo(tokenInfo.getAccess_token()).getAccount().getEmail();
    }
}
