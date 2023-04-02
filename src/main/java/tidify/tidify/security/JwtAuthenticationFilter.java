package tidify.tidify.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tidify.tidify.redis.RedisTokenService;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisTokenService redisTokenService;

    public final String X_AUTH_TOKEN = "X-AUTH-TOKEN";
    private final String REFRESH_TOKEN = "refreshToken";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        String accessToken = resolveAccessToken((HttpServletRequest)request);
        String refreshToken = resolveRefreshToken((HttpServletRequest)request);

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            setAuthentication(accessToken);
        } else if (!jwtTokenProvider.validateToken(accessToken) && refreshToken != null) {
            boolean validateRefreshToken = jwtTokenProvider.validateRefreshToken(refreshToken);
            boolean isRefreshToken = redisTokenService.existsRefreshToken(refreshToken);
            HttpServletResponse httpServletResponse = (HttpServletResponse)response;

            // AT 만료 & RT 유효
            if (validateRefreshToken && isRefreshToken) {
                String newAccessToken = reIssueAccessToken(refreshToken);
                setHeaders(httpServletResponse, newAccessToken, refreshToken);
                redisTokenService.saveTokenInRedis(refreshToken);
            }

            // AT & RT 만료
            if (!validateRefreshToken && isRefreshToken) {
                String newRefreshToken = jwtTokenProvider.reIssueRefreshToken(refreshToken);
                String newAccessToken = jwtTokenProvider.reIssueAccessToken(newRefreshToken);
                setHeaders(httpServletResponse, newAccessToken, newRefreshToken);
                redisTokenService.saveTokenInRedis(newRefreshToken);
            }

            // 왜 굳이 redis 로 가서 refreshToken 으로 조회하는거지?
            // 자바 소스에서 refreshToken 으로 userEmail 을 가져올 수 있는데?
        }
        chain.doFilter(request, response);
    }

    private void reIssueBothTokens(String refreshToken, HttpServletResponse httpServletResponse) {

        String newRefreshToken = jwtTokenProvider.reIssueRefreshToken(refreshToken);
        String newAccessToken = jwtTokenProvider.reIssueAccessToken(newRefreshToken);
        redisTokenService.saveTokenInRedis(newRefreshToken);
        setHeaders(httpServletResponse, newAccessToken, newRefreshToken);
    }

    private String reIssueAccessToken(String refreshToken) {
        String userEmail = redisTokenService.getRedisValue(refreshToken);
        return jwtTokenProvider.createAccessToken(userEmail);
    }

    private void setHeaders(HttpServletResponse httpServletResponse, String accessToken, String refreshToken) {
        httpServletResponse.setHeader(REFRESH_TOKEN, refreshToken);
        httpServletResponse.setHeader(X_AUTH_TOKEN, accessToken);
        this.setAuthentication(accessToken);
    }

    public void setAuthentication(String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public String resolveAccessToken(HttpServletRequest request) {
        return request.getHeader(X_AUTH_TOKEN);
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        return request.getHeader(REFRESH_TOKEN);
    }
}