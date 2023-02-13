package tidify.tidify.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KAKAOTokenRequire {

    private String code;
    private String client_id;
    private String client_secret;
    private String redirect_uri;
    private final String grant_type = "authorization_code";

    public static KAKAOTokenRequire of(KakaoInfo googleInfo, String code){
        // return KAKAOTokenRequire.builder()
        //     .client_id(googleInfo.getClientId())
        //     .client_secret(googleInfo.getSecretKey())
        //     .redirect_uri(googleInfo.getRedirectUri())
        //     .code(code)
        //     .build();
        return null;
    }

    public static KAKAOTokenRequire of(String code){
        return KAKAOTokenRequire.builder()
            .client_id("c3a459def82fa127f7238d078ece3a8e")
            .redirect_uri("http://localhost:8080/app/users/kakao")
            .code(code)
            .build();
    }

    // kakao는 Content-Type 을 application/x-www-form-urlencoded 로 받는다.
    // FeignClient는 기본이 JSON으로 변경하니 아래처럼 데이터를 변환 후 보내야 한다.
    @Override
    public String toString() {
        return
            // "code=" + code + '&' +
            "grant_type=" + grant_type + '&' +
                "client_id=" + client_id + '&' +
                // "client_secret=" + client_secret + '&' +
                "redirect_uri=" + redirect_uri + '&' +
                "code=" + code;

    }

}
