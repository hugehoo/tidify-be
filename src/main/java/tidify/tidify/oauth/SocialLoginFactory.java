package tidify.tidify.oauth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import tidify.tidify.domain.SocialType;
import tidify.tidify.domain.User;
import tidify.tidify.dto.UserDto;

@Component
public class SocialLoginFactory {

    private final Map<SocialType, Function<UserDto, User>> socialMap = new HashMap<>();
    private final Map<SocialType, Function<String, String>> emailMap = new HashMap<>();

    public SocialLoginFactory(List<SocialLogin> socialList) {
        if (CollectionUtils.isEmpty(socialList)) {
            throw new IllegalArgumentException();
        }
        for (SocialLogin social : socialList) {
            this.socialMap.put(social.getSocialType(), social::userTransaction);
            this.emailMap.put(social.getSocialType(), social::emailTransaction);
        }
    }

    public Function<UserDto, User> getSocialType(SocialType socialType) {
        return socialMap.get(socialType);
    }

    public Function<String, String> getEmail(SocialType socialType) {
        return emailMap.get(socialType);
    }
}
