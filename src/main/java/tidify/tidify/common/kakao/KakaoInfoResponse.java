package tidify.tidify.common.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoInfoResponse {
    private Long id;
    private KAKAOProperty properties;

    @JsonProperty(value = "kakao_account")
    private KAKAOProperty account;

}
