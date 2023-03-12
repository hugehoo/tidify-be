package tidify.tidify.controller;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tidify.tidify.security.Token;
import tidify.tidify.domain.SocialType;
import tidify.tidify.service.AccountService;

@RestController
@RequiredArgsConstructor
@RequestMapping("oauth2/")
public class OAuthController {

    private final AccountService userService;

    @GetMapping("login")
    public Token getKakaoToken(@RequestParam(required = false) String code,
        @RequestParam(required = false) SocialType type,
        HttpServletRequest request) {
        code = Objects.requireNonNullElse(request.getHeader("Authorization"), code);
        return userService.getJWTTokens(code, type);
    }
}
