package tidify.tidify.oauth;

import tidify.tidify.domain.SocialType;
import tidify.tidify.domain.User;
import tidify.tidify.dto.UserDto;

public interface SocialLogin {

    SocialType getSocialType();

    User userTransaction(UserDto userDto);

    String emailTransaction(String identityCode);

}
