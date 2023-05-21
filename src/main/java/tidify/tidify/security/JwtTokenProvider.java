package tidify.tidify.security;

import static tidify.tidify.common.Constants.*;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tidify.tidify.domain.SocialType;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;

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
        try {
            Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
            return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody().getSubject();
        } catch (ExpiredJwtException e) {
            Claims claims = e.getClaims();
            return claims.getSubject();
        }
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

}