package tidify.tidify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {
    private String email;
    private String password;
    private String accessToken;
    private String refreshToken;
}
