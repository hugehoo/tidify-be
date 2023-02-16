package tidify.tidify.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// @EnableWebSecurity
public class OAuth2SecurityConfig {
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     http.oauth2Login(oauth2 -> oauth2
    //         .authorizationEndpoint(authorization ->
    //             authorization.baseUri("/oauth2/authorization")
    //                 .authorizationRequestRepository(this.authorizationRequestRepository())
    //         )
    //         .redirectionEndpoint(redirection ->
    //             redirection.baseUri("/*/oauth2/code/*")
    //         )
    //         .userInfoEndpoint(userInfo ->
    //             userInfo.userService(this.oauth2UserService())
    //         )
    //         .successHandler(oAuth2AuthenticationSuccessHandler())
    //         .failureHandler(oAuth2AuthenticationFailureHandler())
    //     );
    //     return http.build();
    // }
}