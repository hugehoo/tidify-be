package tidify.tidify.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoSample {
    private Long id;
    private KAKAOProperty properties;

    @JsonProperty(value = "kakao_account")
    private KAKAOProperty account;

    // @JsonProperty(value = "has_email")
    // private boolean isEmail;
    // private String email;

}
