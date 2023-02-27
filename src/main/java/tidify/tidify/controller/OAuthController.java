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
    /**
     * OAuth Login 시 인증 코드를 넘겨받은 후 첫 로그인 시 회원가입
     */
    // @GetMapping("/login/oauth/{provider}")
    // public ResponseEntity<?> login(
    //     @PathVariable String provider, @RequestParam String code
    // ) {
    //     // userService.login(provider, code);
    // }

    // @GetMapping("callback")
    // public String login(@AuthenticationPrincipal User user) {
    //
    //     System.out.println(user);
    //     return null;
    // }

    @GetMapping("authorize")
    public Token KAKAORedirect(@RequestParam String code) {
    // @GetMapping("login/kakao")
    // public String KAKAORedirect(@RequestParam String code) {

        // 여긴 앱의 역할 이 아니네..?
        // 카카오 로그인 화면에서 로그인
        // -> 발급받은 code 로 여기 api 주소 찌름.
        // 그걸 매개로 token 을 발급 받는다 .
        // return userService.getKAKAOAccessTokenFeign(code);
        // userService.createKakaoUser(KAKAOAccessTokenFeign.getAccess_token());
        return userService.getKAKAOAccessTokenFeign(code);
    }

    @GetMapping("refresh")
    public Token refreshToken(@RequestParam String refreshToken) {
        //User user = userRepo.findByRefresh(refresh) ㄴㄴ kakao 서버에 바로 보내야하는거 아닌가?

        // return userService.getKAKAOAccessTokenFeign(code);
        return null;
    }

    // @GetMapping("authorize/kakao")
    // public void KAKAORedirectTest() {
    //     System.out.println("would be work");
    // }
}
