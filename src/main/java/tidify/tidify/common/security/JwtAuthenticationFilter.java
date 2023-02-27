package tidify.tidify.common.security;

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

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        String accessToken = jwtTokenProvider.resolveToken((HttpServletRequest)request);
        String refreshToken = jwtTokenProvider.resolveRefreshToken((HttpServletRequest)request);
        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            setAuthentication(accessToken);
        }
        else if (!jwtTokenProvider.validateToken(accessToken) && refreshToken != null) {
            // 재발급 후, 컨텍스트에 다시 넣기
            /// 리프레시 토큰 검증
            boolean validateRefreshToken = jwtTokenProvider.validateRefreshToken(refreshToken);
            /// 리프레시 토큰 저장소 존재유무 확인
            boolean isRefreshToken = jwtTokenProvider.existsRefreshToken(refreshToken);
            if (validateRefreshToken && isRefreshToken) {
                /// 리프레시 토큰으로 이메일 정보 가져오기
                String email = jwtTokenProvider.getUserPk(refreshToken, true);
                // 이메일로 권한정보 받아오기
                // List<String> roles = jwtTokenProvider.getRoles(email);

                /// 토큰 발급
                String newAccessToken = jwtTokenProvider.createAccessToken(email);
                /// 헤더에 어세스 토큰 추가
                jwtTokenProvider.setHeaderAccessToken((HttpServletResponse)response, newAccessToken);
                /// 컨텍스트에 넣기
                this.setAuthentication(newAccessToken);
            }
        }
        chain.doFilter(request, response);
    }

    // SecurityContext 에 Authentication 객체를 저장합니다.
    public void setAuthentication(String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}