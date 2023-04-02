package tidify.tidify.security;

import lombok.Builder;
import lombok.Getter;
import tidify.tidify.domain.SocialType;

@Getter
@Builder
public class Token {
    private String accessToken;
    private String refreshToken;
    private String key;
    private SocialType type;

}
