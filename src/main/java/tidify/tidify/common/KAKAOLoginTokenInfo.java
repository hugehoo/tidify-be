package tidify.tidify.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KAKAOLoginTokenInfo {

    private String access_token;
    private String expires_in;
    private String refresh_token;
    private String scope;
    private String token_type;
    private String id_token;
    private String refresh_token_expires_in;

    public String getAccess_token() {
        return "Bearer " + access_token;
    }

    @Override
    public String toString() {
        return "KAKAOLoginToken{" +
            "access_token='" + access_token + '\'' +
            ", expires_in='" + expires_in + '\'' +
            ", refresh_token='" + refresh_token + '\'' +
            ", scope='" + scope + '\'' +
            ", token_type='" + token_type + '\'' +
            ", id_token='" + id_token + '\'' +
            ", refresh_token_expires_in='" + refresh_token_expires_in + '\'' +
            '}';
    }
}
