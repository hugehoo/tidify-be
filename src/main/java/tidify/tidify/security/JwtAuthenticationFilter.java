package tidify.tidify.security;

import static tidify.tidify.common.Constants.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tidify.tidify.redis.RedisTokenService;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisTokenService redisTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String accessToken = resolveAccessToken(request);
        String refreshToken = resolveRefreshToken(request);

        if (jwtTokenProvider.validateToken(accessToken)) {
            setAuthentication(accessToken);
        } else {
            // filter 용 exception handler 찾아보고, 거기서 예외처리할것. unchecked Exception 에서 try~catch 불필요
            try {
                rotateTokens(request, response, refreshToken);
            } catch (MalformedJwtException | IllegalArgumentException e) {
                log.info("[Request URI] {} {}", request.getMethod(), request.getRequestURI());
                log.info("[Request Principal] {} ", request.getUserPrincipal());
                return;
            }
        }
        //TODO : doFilter 실패하면 예외 터진다. -> 핸들러에서 잡아줘야해
        filterChain.doFilter(request, response);
    }

    private void rotateTokens(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        Map<String, String> map = new HashMap<>();
        String userPk = jwtTokenProvider.getUserPk(refreshToken, true); // 여기서 지속적으로 터지는데,
        reIssueBothTokens(userPk, map);
        setHeaders(response, map);
        setAuthentication(map.get(X_AUTH_TOKEN));
    }

    private void reIssueBothTokens(String userPk, Map<String, String> map) {
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userPk);
        String newAccessToken = jwtTokenProvider.createAccessToken(userPk);
        map.put(X_AUTH_TOKEN, newAccessToken);
        map.put(REFRESH_TOKEN, newRefreshToken);
    }

    private boolean isTokensIssued(Map<String, String> map) {
        return !map.isEmpty();
    }

    private void setHeaders(HttpServletResponse response, Map<String, String> map) {
        response.setHeader(REFRESH_TOKEN, map.get(REFRESH_TOKEN));
        response.setHeader(X_AUTH_TOKEN, map.get(X_AUTH_TOKEN));
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