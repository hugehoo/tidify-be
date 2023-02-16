package tidify.tidify.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tidify.tidify.common.KAKAOFeign;
import tidify.tidify.common.KAKAOLoginTokenInfo;
import tidify.tidify.common.KakaoSample;
import tidify.tidify.repository.UserRepository;
import tidify.tidify.security.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final InMemoryClientRegistrationRepository inMemoryRegistrationRepository;
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String userInfoUri;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String grantType;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    private final KAKAOFeign kakaoFeign;
    private final UserRepository userRepository;

    public KAKAOLoginTokenInfo getKAKAOAccessTokenFeign(String authorizationCode) {
        // value annotation 으로 부르면 되는데 굳이..?
        ClientRegistration kakao = inMemoryRegistrationRepository.findByRegistrationId("kakao");
        // kakaoFeign.getAccessToken(grantType, clientId, redirectUri, authorizationCode);
        KAKAOLoginTokenInfo tokenInfo = kakaoFeign.getAccessToken(grantType, clientId, redirectUri, authorizationCode);
        // new OAuth2UserRequest(kakao, new OAuth2AccessToken("Bearer", tokenInfo.getAccess_token(), tokenInfo.getIssuedAt(), tokenInfo.getExpires_in()))

        User kakaoUser = createKakaoUser(tokenInfo);
        Optional<User> user = userRepository.findWithUserRolesByEmailAndDel(kakaoUser.getEmail(), false);
        if (!user.isPresent()) {
            kakaoUser = userRepository.save(kakaoUser);
        }
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

    public User createKakaoUser(KAKAOLoginTokenInfo kakaoToken) {

        try {
            URL url = new URL(userInfoUri); // user-info-uri
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", kakaoToken.getAccess_token());

            //요청을 통해 얻은 JSON 타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            System.out.println("response body : " + result);

            ObjectMapper mapper = new ObjectMapper();
            KakaoSample kakaoSample = mapper.readValue(result.toString(), KakaoSample.class);
            // User user = userRepository.save(new User(kakaoSample, "111", kakaoToken));
            br.close();
            return new User(kakaoSample, "111", kakaoToken);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
