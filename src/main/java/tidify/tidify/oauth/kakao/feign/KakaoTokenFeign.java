package tidify.tidify.oauth.kakao.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import feign.Headers;
import tidify.tidify.oauth.kakao.KakaoLoginTokenInfo;

@FeignClient(name = "KAKAOInfoFeignClient", url = "https://kauth.kakao.com")
public interface KakaoTokenFeign {

    @PostMapping("/oauth/token")
    @Headers("Content-Type: application/x-www-form-urlencoded") // 얘가 문제였구나
    KakaoLoginTokenInfo getAccessToken(
        @RequestParam("grant_type") String grantType,
        @RequestParam("client_id") String restApiKey,
        @RequestParam("redirect_uri") String redirectUrl,
        @RequestParam("code") String code
    );

}
