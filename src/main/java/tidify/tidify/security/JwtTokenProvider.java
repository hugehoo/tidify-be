package tidify.tidify.security;

import static tidify.tidify.common.Constants.*;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tidify.tidify.domain.SocialType;
import tidify.tidify.domain.User;
import tidify.tidify.repository.UserRepository;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

import java.util.Base64;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;

    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.refresh-secret}")
    private String refreshSecretKey;

    List<String> roles = List.of("User", "Admin");

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public Token createToken(String userEmail, SocialType type) {
        String accessToken = createAccessToken(userEmail);
        String refreshToken = createRefreshToken(userEmail);

        return Token.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .key(userEmail)
            .type(type)
            .build();
    }

    public String createRefreshToken(String userEmail) {
        Claims claims = Jwts.claims().setSubject(userEmail);
        claims.put("roles", roles);
        Date now = new Date();

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME))
            .signWith(SignatureAlgorithm.HS256, refreshSecretKey)
            .compact();
    }

    // JWT 토큰에서 인증 정보 조회 -> JwtAuthenticationFilter.getAuthentication() 메서드가 아닌 여기 메서드 타는 이유는 뭘까?
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token, false));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserPk(String token, boolean isRefreshToken) {
        String signingKey = isRefreshToken ? refreshSecretKey : secretKey;
        return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(refreshToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String createAccessToken(String userName) {

        Claims claims = Jwts.claims().setSubject(userName); // JWT payload 에 저장되는 정보단위
        claims.put("roles", roles); // 정보는 key / value 쌍으로 저장된다.
        Date now = new Date();

        return Jwts.builder()
            .setClaims(claims) // 정보 저장
            .setIssuedAt(now) // 토큰 발행 시간 정보
            .setExpiration(new Date(now.getTime() + TOKEN_VALID_TIME))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    @Transactional
    public String reIssueRefreshToken(String refreshToken) {
        // refreshToken 재발급은 로그아웃 하기로 하지 않았나?
        // 굳이 RDB 조회할 필요 있나 -> DB User refreshToken 값 Update 해줘야함.
        User user = userRepository.findUserByRefreshToken(refreshToken);
        // refreshToken 으로도 userEmail 은 꺼낼 수 있다.
        String newRefreshToken = createRefreshToken(user.getUserEmail());
        user.modifyRefreshToken(newRefreshToken);
        return newRefreshToken;
    }

    public String reIssueAccessToken(String refreshToken) {
        String email = getUserPk(refreshToken, true);
        return createAccessToken(email);
    }
}