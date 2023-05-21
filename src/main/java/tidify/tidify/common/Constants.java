package tidify.tidify.common;

import javax.validation.constraints.Min;

public class Constants {

    public static final long MINUTE = 60;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
    // public static final long TOKEN_VALID_TIME = MINUTE; // x

    // public static final long TOKEN_VALID_TIME = HOUR * 1000L; // 1시간 (테스트용)
    public static final long TOKEN_VALID_TIME = DAY * 500L; // 12시간 (상용)
    // public static final long TOKEN_VALID_TIME = DAY * 1000L; // 24시간 (테스트용)
    // public static final long REFRESH_TOKEN_VALID_TIME = HOUR * 1000L; // 1시간 (테스트용)
    public static final long REFRESH_TOKEN_VALID_TIME = 60 * DAY * 1000L; // 60일 (상용)
    public static final String X_AUTH_TOKEN = "X-AUTH-TOKEN";
    public static final String REFRESH_TOKEN = "refreshToken";

}
