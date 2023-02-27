package tidify.tidify.common.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class KAKAOProperty {
    @JsonProperty("nickname")
    private String nickName;
    @JsonProperty("profile_image")
    private String profileImage;
    @JsonProperty("thumbnail_image")
    private String thumbnailImage;

    @JsonProperty(value = "has_email")
    private boolean isEmail;
    private String email;
}
