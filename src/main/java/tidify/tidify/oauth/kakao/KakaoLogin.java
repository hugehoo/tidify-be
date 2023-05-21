package tidify.tidify.oauth.kakao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tidify.tidify.oauth.SocialLogin;
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

    private final KakaoUserFeign kakaoUserFeign;

    @Override
    public SocialType getSocialType() {
        return SocialType.KAKAO;
    }

    @Override
    public User userTransaction(UserDto user) {
        return User.ofSocialType(
            user.getEmail(),
            user.getPassword(),
            user.getAccessToken(),
            user.getRefreshToken(),
            SocialType.KAKAO
        );
    }

    @Override
    public String emailTransaction(String accessToken) {
        String authorization = String.format("Bearer %s", accessToken);
        return kakaoUserFeign.getUserInfo(authorization).getAccount().getEmail();
    }
}
