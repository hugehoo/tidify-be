package tidify.tidify.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tidify.tidify.domain.User;
import tidify.tidify.dto.ObjectResponseDto;
import tidify.tidify.dto.ResponseDto;
import tidify.tidify.security.Token;
import tidify.tidify.domain.SocialType;
import tidify.tidify.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("oauth2/")
public class OAuthController {

    private final UserService userService;

    @GetMapping("login")
    public ObjectResponseDto<Token> loginOauth2(@RequestParam SocialType type,
        HttpServletRequest request) {
        String code = request.getHeader("Authorization");
        Token authenticate = userService.getAuthenticate(code, type);
        return new ObjectResponseDto<>(authenticate);
    }

    @GetMapping("loginV2")
    public ObjectResponseDto<Token> loginOauth2V2(@RequestParam SocialType type,
        HttpServletRequest request) {
        String code = request.getHeader("Authorization");
        Token authenticate = userService.getAuthenticate(code, type);
        return new ObjectResponseDto<>(authenticate);
    }

    @DeleteMapping("withdrawal")
    public ResponseDto userWithdraw(@AuthenticationPrincipal User user) {
        userService.userWithdraw(user);
        return ResponseDto.ofDeleteApi();
    }
}
