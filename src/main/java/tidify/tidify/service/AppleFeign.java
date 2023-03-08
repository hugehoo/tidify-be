package tidify.tidify.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import feign.Headers;
import tidify.tidify.common.Constants;
import tidify.tidify.dto.AppleDto;

@FeignClient(name = "AppleFeign", url = Constants.APPLE_AUTH_URL)
public interface AppleFeign {

    @GetMapping
    @Headers("Content-Type: application/x-www-form-urlencoded")
    AppleDto.AppleKeysResponse getAccessToken();

}
