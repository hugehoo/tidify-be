package tidify.tidify.oauth.kakao.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import feign.Headers;
import tidify.tidify.oauth.kakao.KakaoInfoResponse;

@FeignClient(name = "KAKAOUserFeignClient", url = "https://kapi.kakao.com")
public interface KakaoUserFeign {

    @PostMapping("/v2/user/me")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    KakaoInfoResponse getUserInfo(@RequestHeader(value = "Authorization", required = true) String accessToken);

}
