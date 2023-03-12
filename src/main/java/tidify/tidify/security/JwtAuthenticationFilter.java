package tidify.tidify.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisTemplate<String, String> redisTemplate;

    private ValueOperations<String, String> redis() {
        return redisTemplate.opsForValue();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        String accessToken = resolveToken((HttpServletRequest)request);
        String refreshToken = resolveRefreshToken((HttpServletRequest)request);

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            setAuthentication(accessToken);
        } else if (!jwtTokenProvider.validateToken(accessToken) && refreshToken != null) {
            boolean validateRefreshToken = jwtTokenProvider.validateRefreshToken(refreshToken);
            // 이걸 기존엔 rdb 에서 조회하는데, redis 로 하자 ->  레디스가 보장을 못해줘.
            // -> expire 되면 false 뜨기 때문에 로그아웃시켜야함?
            // boolean isRefreshToken = isExistRedisToken(refreshToken);
            boolean isRefreshToken = jwtTokenProvider.existsRefreshToken(refreshToken);
            HttpServletResponse httpServletResponse = (HttpServletResponse)response;
            // AT 만료 & RT 유효
            if (validateRefreshToken && isRefreshToken) {
                reIssueAccessToken(refreshToken, httpServletResponse);
            }
            // AT & RT 만료
            if (!validateRefreshToken && isRefreshToken) {
                reIssueBothTokens(refreshToken, httpServletResponse);
            }

            // refreshToken 이 만료되면?
        }
        chain.doFilter(request, response);
    }

    private void reIssueBothTokens(String refreshToken, HttpServletResponse httpServletResponse) {

        String newRefreshToken = jwtTokenProvider.reIssueRefreshToken(refreshToken);
        String newAccessToken = jwtTokenProvider.reIssueAccessTokenByRefreshToken(httpServletResponse, newRefreshToken);

        httpServletResponse.setHeader("refreshToken", newRefreshToken);
        httpServletResponse.setHeader("X-AUTH-TOKEN", newAccessToken);
        this.setAuthentication(newAccessToken);
    }

    private void reIssueAccessToken(String refreshToken, HttpServletResponse httpServletResponse) {
        String newAccessToken = jwtTokenProvider.reIssueAccessTokenByRefreshToken(httpServletResponse, refreshToken);// setAuthenticationWithNewToken(refreshToken, httpServletResponse);
        httpServletResponse.setHeader("X-AUTH-TOKEN", newAccessToken);
        this.setAuthentication(newAccessToken);
    }

    private void deleteRedisKey(String refreshToken) {
        redis().getAndDelete(refreshToken);
    }

    private boolean isExistRedisToken(String refreshToken) {
        
        return redis().get(refreshToken) != null;
    }

    private void setRedisToken(String accessToken, String refreshToken) {

        redis().set(refreshToken, accessToken);
    }

    public void setAuthentication(String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        if (request.getHeader("refreshToken") != null) {
            return request.getHeader("refreshToken");
        }
        return null;
    }
}