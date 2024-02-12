package tidify.tidify.security;

import tidify.tidify.domain.SocialType;

public record Token(String accessToken, String refreshToken, String key, SocialType type) { }
