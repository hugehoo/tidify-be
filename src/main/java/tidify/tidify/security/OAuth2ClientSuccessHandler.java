package tidify.tidify.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2ClientSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    //    private final SocialService socialService;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws
        IOException,
        ServletException {
        System.out.println("request = " + request);
        System.out.println("response = " + response);
        System.out.println("authentication = " + authentication);
        //        socialService.getInfo((OAuth2AuthenticationToken) authentication);
        //        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        //        String id = String.valueOf(oauth2User.getAttributes().get("id"));

        //        StringBuilder targetUrl = new StringBuilder();
        //        targetUrl.append(httpHttps + serviceDomain);
        //        targetUrl.append("/success");
        //        targetUrl.append("?access_token=").append(resultMap.get("access_token"));
        //        targetUrl.append("&refresh_token=").append(resultMap.get("refresh_token"));
        //        targetUrl.append("&token_type=").append(resultMap.get("token_type"));
        //        targetUrl.append("&expires_in=").append(resultMap.get("expires_in"));
        //        targetUrl.append("&scope=").append(resultMap.get("scope"));
        //        targetUrl.append("&jti=").append(resultMap.get("jti"));
        //
        //        response.sendRedirect(targetUrl.toString());
    }
}

