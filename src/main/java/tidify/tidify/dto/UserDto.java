package tidify.tidify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tidify.tidify.security.Token;

@Getter
@AllArgsConstructor
public class UserDto {
    private String email;
    private String password;
    private String accessToken;
    private String refreshToken;

    public static UserDto of(String email, String password, Token token) {
        return new UserDto(email, password, token.getAccessToken(), token.getRefreshToken());
    }
}
