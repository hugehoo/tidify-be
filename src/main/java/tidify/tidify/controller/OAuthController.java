package tidify.tidify.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tidify.tidify.common.KAKAOLoginTokenInfo;
import tidify.tidify.service.AccountService;

@RestController
@RequiredArgsConstructor
@RequestMapping("app/users/")
public class OAuthController {

    private final AccountService userService;

    @GetMapping("kakao")
    public String KAKAORedirect(@RequestParam String code) {

        // 여긴 앱의 역할 이 아니네..?
        // 카카오 로그인 화면에서 로그인
        // -> 발급받은 code 로 여기 api 주소 찌름.
        // 그걸 매개로 token 을 발급 받는다 .
        KAKAOLoginTokenInfo KAKAOAccessTokenFeign = userService.getKAKAOAccessTokenFeign(code);
        //


        // userService.createKakaoUser(KAKAOAccessTokenFeign.getAccess_token());

        return KAKAOAccessTokenFeign.getAccess_token();
    }


}
