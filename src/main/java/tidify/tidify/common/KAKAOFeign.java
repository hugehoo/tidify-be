package tidify.tidify.common;

import java.net.URI;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import feign.Headers;

// baseUrl: https://kauth.kakao.com
@FeignClient(name = "KAKAOInfoFeignClient", url = "https://kauth.kakao.com")
public interface KAKAOFeign {

    @PostMapping("/oauth/token")
    @Headers("Content-Type: application/x-www-form-urlencoded") // 얘가 문제였구나
    KAKAOLoginTokenInfo getAccessToken(
        @RequestParam("grant_type") String grantType,
        @RequestParam("client_id") String restApiKey,
        @RequestParam("redirect_uri") String redirectUrl,
        @RequestParam("code") String code
    );

    @PostMapping
    KakaoInfo getUser(URI uri, @RequestHeader(name = "Authorization") String token);
}
