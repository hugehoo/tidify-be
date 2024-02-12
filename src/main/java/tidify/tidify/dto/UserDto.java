package tidify.tidify.dto;

import tidify.tidify.security.Token;

public record UserDto(String email, String password, String accessToken, String refreshToken) {

    public static UserDto of(String email, String password, Token token) {
        return new UserDto(email, password, token.accessToken(), token.refreshToken());
    }
}
