package tidify.tidify.oauth.apple;

import java.text.ParseException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;
import tidify.tidify.dto.Payload;

@Component
@RequiredArgsConstructor
public class AppleUtils {

    public Payload userIdFromApple(String id_Token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(id_Token);
            ReadOnlyJWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            return getPayload(claimsSet);
        } catch (JsonProcessingException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static Payload getPayload(ReadOnlyJWTClaimsSet claimsSet) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(claimsSet.toJSONObject().toJSONString(), Payload.class);
    }
}
