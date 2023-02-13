package tidify.tidify.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tidify.tidify.common.KAKAOFeign;
import tidify.tidify.common.KAKAOLoginTokenInfo;
import tidify.tidify.common.KakaoAccount;
import tidify.tidify.common.KakaoSample;
import tidify.tidify.domain.Account;
import tidify.tidify.domain.SocialType;
import tidify.tidify.repository.AccountRepository;
import tidify.tidify.repository.UserRepository;
import tidify.tidify.security.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final KAKAOFeign kakaoFeign;
    private final UserRepository userRepository;

    public KAKAOLoginTokenInfo getKAKAOAccessTokenFeign(String code) {

        KAKAOLoginTokenInfo tokenInfo = kakaoFeign.getAccessToken(
            "authorization_code",
            "c3a459def82fa127f7238d078ece3a8e",
            "http://localhost:8080/app/users/kakao",
            code
        );
        System.out.println(tokenInfo.getAccess_token());
        System.out.println(tokenInfo.getRefresh_token());
        System.out.println(tokenInfo.getExpires_in());

        createKakaoUser(tokenInfo);
        return tokenInfo;
    }

    // public String getKaKaoAccessToken(String code) {
    //     String access_Token = "";
    //     String refresh_Token = "";
    //     String reqURL = "https://kauth.kakao.com/oauth/token";
    //
    //     try {
    //         URL url = new URL(reqURL);
    //         HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    //
    //         //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
    //         conn.setRequestMethod("POST");
    //         conn.setDoOutput(true);
    //
    //         //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
    //         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
    //         StringBuilder sb = new StringBuilder();
    //         sb.append("grant_type=authorization_code");
    //         sb.append("&client_id=c3a459def82fa127f7238d078ece3a8e"); // TODO REST_API_KEY 입력
    //         sb.append("&redirect_uri=http://localhost:8080/app/users/kakao"); // TODO 인가코드 받은 redirect_uri 입력
    //         sb.append("&code=" + code); // c3a459def82fa127f7238d078ece3a8e
    //         bw.write(sb.toString());
    //         bw.flush();
    //
    //         //결과 코드가 200이라면 성공
    //         int responseCode = conn.getResponseCode();
    //         System.out.println("responseCode : " + responseCode);
    //         //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
    //         BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    //         String line = "";
    //         String result = "";
    //
    //         while ((line = br.readLine()) != null) {
    //             result += line;
    //         }
    //         System.out.println("response body : " + result);
    //
    //         //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
    //         JsonParser parser = new JsonParser();
    //         JsonElement element = parser.parse(result);
    //
    //         access_Token = element.getAsJsonObject().get("access_token").getAsString();
    //         refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();
    //
    //         System.out.println("access_token : " + access_Token);
    //         System.out.println("refresh_token : " + refresh_Token);
    //
    //         br.close();
    //         bw.close();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    //
    //     return access_Token;
    // }

    public void createKakaoUser(KAKAOLoginTokenInfo kakaoToken) {

        try {
            URL url = new URL("https://kapi.kakao.com/v2/user/me");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", kakaoToken.getAccess_token());

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON 타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            System.out.println("response body : " + result);

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

            String content = result.toString();
            KakaoSample kakaoSample = mapper.readValue(content, KakaoSample.class);
            System.out.println(kakaoSample.toString());

            //Gson 라이브러리로 JSON 파싱
            // JsonParser parser = new JsonParser();
            // JsonElement element = parser.parse(result.toString());

            // Long kakaoUserId = element.getAsJsonObject().get("id").getAsLong();
            // JsonElement kakaoAccount = element.getAsJsonObject().get("kakao_account");
            // JsonElement has_email = kakaoAccount.getAsJsonObject().get("has_email");
            // boolean hasEmail = has_email.getAsBoolean();
            // String email = "";
            // if (kakaoSample.getAccount().isEmail()) {
            //     email = kakaoSample.getAccount().getEmail();
            // }
            // JsonObject profile = kakaoAccount.getAsJsonObject()
            //     .get("profile")
            //     .getAsJsonObject();
            // String nickName = profile
            //     .get("nickname")
            //     .getAsString();
            // String thumbnailImage = profile
            //     .get("thumbnail_image_url")
            //     .getAsString();

            // System.out.println("id : " + kakaoUserId);
            // System.out.println("email : " + email);
            // System.out.println("nickName = " + nickName);
            // System.out.println("thumbnailImage = " + thumbnailImage);
            User user = User.KAKAO()
                .kakao(kakaoSample)
                .password("111")
                .token(kakaoToken)
                .build();
            User save = userRepository.save(user);
            System.out.println(save);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public KakaoInfo getInfo(final String token) {
    //     log.debug("token = {}", token);
    //     try {
    //         return kakaoFeign.getUser(new URI("https://kauth.kakao.com/v2/user/me"), token);
    //     } catch (Exception e) {
    //         log.error("something error..", e);
    //         return KakaoInfo.fail();
    //     }
    // }

    // public Object getKakaoTokenWithInfo(String code) {
    //     String userId = SocialType.K.getType() +"_" + getKakaoInfo(code).getId();
    //     Users users = userRepository.findByLoginId(userId).orElse(null);
    //     if(users == null){
    //         return SocialInfoRes.newInstance(userId,socialRandomPassword(userId),SocialType.K);
    //     }
    //     return createToken(users);
    // }
    // private KTokenInfoRes getKakaoInfo(String code) {
    //     return kakaoInfoFeignClient
    //         .getInfo(
    //             kakaoLoginFeignClient
    //                 .getToken(
    //                     KLoginTokenReq.newInstance(kakaoInfo, code).toString())
    //                 .getAccess_token());
    // }
    // private String socialRandomPassword(String userId) {
    //     String systemMil = String.valueOf(System.currentTimeMillis());
    //     return passwordEncoder.encode(userId + systemMil);
    // }
    //
    // private LoginRes createToken(Users user) {
    //     return LoginRes.of(jwtProvider.createAccessToken(user.getLoginId(), user.getGroup().getFuncList()), jwtProvider.createRefreshToken(user.getLoginId()));
    // }
}
