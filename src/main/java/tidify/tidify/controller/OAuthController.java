package tidify.tidify.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tidify.tidify.security.Token;
import tidify.tidify.domain.SocialType;
import tidify.tidify.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("oauth2/")
public class OAuthController {

    private final UserService userService;

    @GetMapping("login")
    public Token loginOauth2(@RequestParam SocialType type,
        HttpServletRequest request) {
        String code = request.getHeader("Authorization");
        return userService.getAuthenticate(code, type);
    }
}
