package tidify.tidify.dto;

import lombok.Getter;

@Getter
public class Payload {

    private String iss;
    private String aud;
    private Long exp;
    private Long iat;
    private String sub;
    private String nonce;
    private String c_hash;
    private String at_hash;
    private String email;
    private String email_verified;
    private String is_private_email;
    private Long auth_time;
    private boolean nonce_supported;
    private Integer real_user_status;

    @Override
    public String toString() {
        return "{" +
            "iss='" + iss + '\'' +
            ", aud='" + aud + '\'' +
            ", exp=" + exp +
            ", iat=" + iat +
            ", sub='" + sub + '\'' +
            ", nonce='" + nonce + '\'' +
            ", c_hash='" + c_hash + '\'' +
            ", at_hash='" + at_hash + '\'' +
            ", email='" + email + '\'' +
            ", email_verified='" + email_verified + '\'' +
            ", is_private_email='" + is_private_email + '\'' +
            ", auth_time=" + auth_time +
            ", nonce_supported=" + nonce_supported +
            '}';
    }
}
