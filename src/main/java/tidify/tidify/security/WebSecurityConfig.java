package tidify.tidify.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig
    // extends WebSecurityConfigurerAdapter
{

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().
            requestMatchers(new AntPathRequestMatcher("/api"))
            .requestMatchers(new AntPathRequestMatcher( "/oauth2/login"))
            .requestMatchers(new AntPathRequestMatcher( "/oauth2/loginV2"))
            .requestMatchers(new AntPathRequestMatcher( "/actuator/**"))
            .requestMatchers(new AntPathRequestMatcher( "/root"));
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize ->
                authorize.requestMatchers(new MvcRequestMatcher(introspector, "/**")).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, "/oauth2/login")).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, "/oauth2/loginV2")).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, "/admin/**")).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, "/user/**")).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, "/app/label")).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, "/app/folders")).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, "/oauth2/withdrawal")).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, "/app/bookmarks/**")).permitAll()
                    .anyRequest()
                    .authenticated())
            .httpBasic(Customizer.withDefaults())
            .apply(new JwtSecurityConfig(jwtTokenProvider));
        return http.build();
    }

}
