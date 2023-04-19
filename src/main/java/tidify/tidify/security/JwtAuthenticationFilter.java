package tidify.tidify.security;

import static tidify.tidify.common.Constants.X_AUTH_TOKEN;
import static tidify.tidify.common.Constants.REFRESH_TOKEN;

import java.io.IOException;
import java.util.Map;

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

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse)servletResponse;
        HttpServletRequest request = (HttpServletRequest)servletRequest;

        String accessToken = resolveAccessToken(request);
        String refreshToken = resolveRefreshToken(request);

        if (jwtTokenProvider.validateToken(accessToken)) {
            setAuthentication(accessToken);
        } else {
            rotateTokens(response, refreshToken);
        }
        //TODO : doFilter 실패하면 예외 터진다. -> 핸들러에서 잡아줘야해
        chain.doFilter(servletRequest, servletResponse);
    }

    private void rotateTokens(HttpServletResponse response, String refreshToken) {
        Map<String, String> map = redisTokenService.createBothTokens(refreshToken);
        if (isTokensIssued(map)) {
            setHeaders(response, map);
            setAuthentication(map.get(X_AUTH_TOKEN));
        }
    }

    private boolean isTokensIssued(Map<String, String> map) {
        return !map.isEmpty();
    }

    private void setHeaders(HttpServletResponse httpServletResponse, Map<String, String> map) {
        httpServletResponse.setHeader(REFRESH_TOKEN, map.get(REFRESH_TOKEN));
        httpServletResponse.setHeader(X_AUTH_TOKEN, map.get(X_AUTH_TOKEN));
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