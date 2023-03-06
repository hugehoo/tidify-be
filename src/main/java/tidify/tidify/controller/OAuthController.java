package tidify.tidify.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tidify.tidify.common.security.Token;
import tidify.tidify.service.AccountService;

@RestController
@RequiredArgsConstructor
@RequestMapping("oauth2/")
public class OAuthController {

    private final AccountService userService;


    @GetMapping("login/kakao")
    public Token getKakaoToken(@RequestParam String code) {
        return userService.getKakaoAccessTokenFeign(code);
    }
}
