package tidify.tidify.service;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import tidify.tidify.common.exception.BusinessException;
import tidify.tidify.domain.ErrorCode;
import tidify.tidify.dto.AppleDto;

@Service
@RequiredArgsConstructor
public class AppleService {

    private final AppleFeign appleFeign;

    /**
     * 1. apple로 부터 공개키 3개 가져옴
     * 2. 내가 클라에서 가져온 token String과 비교해서 써야할 공개키 확인 (kid,alg 값 같은 것)
     * 3. 그 공개키 재료들로 공개키 만들고, 이 공개키로 JWT토큰 부분의 바디 부분의 decode하면 유저 정보
     */
    public String userIdFromApple(String idToken) {

        String[] decodeArray = idToken.split("\\.");
        String decodeToken = new String(base64Decode(decodeArray[0]));
        try {
            AppleDto.Header header = getHeader(decodeToken);
            ArrayList<AppleDto.Key> keyList = appleFeign.getAccessToken().getKeys();
            PublicKey publicKey = getPublicKey(header, keyList);
            Claims userInfo = getUserInfo(idToken, publicKey); // email 도 추출 가능
            // System.out.println("userInfo.get(\"email\") = " + userInfo.get("email"));
            return userInfo.get("sub").toString();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private AppleDto.Header getHeader(String decodeToken) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(decodeToken, AppleDto.Header.class);
    }

    private Claims getUserInfo(String idToken, PublicKey publicKey) {
        return Jwts.parser()
            .setSigningKey(publicKey)
            .parseClaimsJws(idToken)
            .getBody();
    }

    private PublicKey getPublicKey(AppleDto.Header header, ArrayList<AppleDto.Key> keyList) {
        String alg = header.getAlg();
        String kid = header.getKid();
        return keyList.stream()
            .filter(data -> data.getKid().equals(kid) && data.getAlg().equals(alg))
            .map(this::generatePublicKey)
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.FAILED_TO_FIND_AVALIABLE_RSA));
    }

    public PublicKey generatePublicKey(AppleDto.Key key) {

        BigInteger n = new BigInteger(1, base64Decode(key.getN()));
        BigInteger e = new BigInteger(1, base64Decode(key.getE()));

        try {
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(publicKeySpec);
        } catch (Exception exception) {
            throw new BusinessException(ErrorCode.FAILED_TO_FIND_AVALIABLE_RSA);
        }
    }

    private byte[] base64Decode(String value) {
        return Base64.getUrlDecoder().decode(value);
    }
}
