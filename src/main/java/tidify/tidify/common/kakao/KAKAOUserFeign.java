package tidify.tidify.common.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import feign.Headers;

@FeignClient(name = "KAKAOUserFeignClient", url = "https://kapi.kakao.com")
public interface KAKAOUserFeign {

    @PostMapping("/v2/user/me")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    KakaoInfoResponse getUserInfo(@RequestHeader(value = "Authorization", required = true) String accessToken);

}
