package tidify.tidify.redis;


import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "RT", timeToLive = 5184000)
public class RefreshToken {

    @Id
    private final String refreshToken;

    private final String userEmail;

    public RefreshToken(final String refreshToken, final String userEmail) {
        this.refreshToken = refreshToken;
        this.userEmail= userEmail;
    }

    public static RefreshToken of(String refreshToken, String userEmail) {
        return new RefreshToken(refreshToken, userEmail);
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getUserEmail() {
        return userEmail;
}
}