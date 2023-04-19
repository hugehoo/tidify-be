package tidify.tidify.redis;


import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Setter;

@RedisHash(value = "RT", timeToLive = 5184000)
public class RefreshToken {

    @Id
    private final String userEmail;

    @Setter
    private String refreshToken;

    public RefreshToken(final String userEmail, final String refreshToken) {
        this.userEmail= userEmail;
        this.refreshToken = refreshToken;
    }

    public static RefreshToken of(String userEmail, String refreshToken) {
        return new RefreshToken(userEmail, refreshToken);
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getUserEmail() {
        return userEmail;
}
}