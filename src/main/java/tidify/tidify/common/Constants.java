package tidify.tidify.common;

public class Constants {

    public static final long MINUTE = 60;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
    public static final long TOKEN_VALID_TIME = DAY * 4000L; // 96시간
    public static final long REFRESH_TOKEN_VALID_TIME = 60 * DAY * 1000L; // 60일
    public static final String X_AUTH_TOKEN = "X-AUTH-TOKEN";
    public static final String REFRESH_TOKEN = "refreshToken";

}
