package tidify.tidify.dto;

import lombok.Getter;

public record Payload(String iss, String aud, Long exp, Long iat, String sub,
                      String nonce, String c_hash, String at_hash, String email, String email_verified,
                      String is_private_email, Long auth_time, boolean nonce_supported, Integer real_user_status) {
}
