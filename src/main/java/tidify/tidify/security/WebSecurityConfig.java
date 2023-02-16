package tidify.tidify.security;

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final SecurityUserService securityUserService;
    private final WebAccessDeniedHandler webAccessDeniedHandler;
    // private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2UserService customOAuth2UserService;

    private final OAuth2ClientSuccessHandler oAuth2ClientSuccessHandler;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/**");

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable(); // ToDO : 얘가 없으면 403 발생

        // http.authorizeRequests()
        //     .antMatchers("/oauth2/authorize", "/api", "/signUp").permitAll();
            // .antMatchers("/app/folder/*").permitAll()
            // .anyRequest().authenticated();

        http
            .oauth2Login()
            .authorizationEndpoint()
            .baseUri("/oauth2/authorize")
            .and()
            .redirectionEndpoint()
            .baseUri("/oauth2/callback")
            .and()
            .userInfoEndpoint()
            .userService(customOAuth2UserService)
            .and()
            .successHandler(oAuth2ClientSuccessHandler);
        // http.oauth2Login()
        //     .authorizationEndpoint()
        //     .baseUri("/app/users/kakao")
        //     .and()
        //     .userInfoEndpoint().userService(customOAuth2UserService);

        // http.oauth2Login()
        //         .loginPage("/app/users/kakao").defaultSuccessUrl("/") // login url 을 따로 만들면 여기를 탈 것이다. 아니네, 지금 url 로도 어쨌든 타야하는 거 아닌감 ㅇㅅㅇ
        //         // .loginPage("/app/users/login")
        //         .userInfoEndpoint().userService(customOAuth2UserService);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(securityUserService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
