package tidify.tidify.common;

public class Constants {

    public static final long MINUTE = 60;
    public static final long HOUR = 60 * MINUTE;

    public static final long DAY = 24 * HOUR;
    public static final long TOKEN_VALID_TIME = DAY * 1000L;
    public static final long REFRESH_TOKEN_VALID_TIME = 60 * DAY * 1000L; // 24시간

    public static final String APPLE_AUTH_URL = "https://appleid.apple.com/auth/keys";
}
