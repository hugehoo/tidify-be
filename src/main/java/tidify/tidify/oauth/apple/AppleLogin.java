package tidify.tidify.oauth.apple;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tidify.tidify.domain.SocialType;
import tidify.tidify.domain.User;
import tidify.tidify.dto.UserDto;
import tidify.tidify.oauth.SocialLogin;

@Component
@RequiredArgsConstructor
public class AppleLogin implements SocialLogin {

    private final AppleUtils appleUtils;

    @Override
    public SocialType getSocialType() {
        return SocialType.APPLE;
    }

    @Override
    public User userTransaction(UserDto user) {
        return User.ofSocialType(user.email(), user.password(), user.accessToken(), user.refreshToken(), SocialType.APPLE);
    }

    @Override
    public String emailTransaction(String identityToken) {
        return appleUtils
            .userIdFromApple(identityToken)
            .email();
    }
}
