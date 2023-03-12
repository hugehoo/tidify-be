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
import tidify.tidify.domain.User;
import tidify.tidify.repository.UserRepository;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

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

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 토큰 생성
    public Token createToken(String userName) {
        String accessToken = createAccessToken(userName);
        String refreshToken = createRefreshToken(userName);

        return Token.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .key(userName)
            .build();
    }

    public String createRefreshToken(String userName) {
        Claims claims = Jwts.claims().setSubject(userName);
        claims.put("roles", roles);
        Date now = new Date();
        Date date = new Date(now.getTime());
        log.info("refreshToken : {}", date);
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

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token, boolean isRefresh) {
        String signingKey = isRefresh ? refreshSecretKey : secretKey;
        return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody().getSubject();
    }

    // 토큰의 유효성 + 만료일자 확인
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

    public boolean existsRefreshToken(String refreshToken) {
        return userRepository.existsByRefreshToken(refreshToken);
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
        User user =  userRepository.findUserByRefreshToken(refreshToken);
        String newRefreshToken = createRefreshToken(user.getUsername()); // 이름 저장 안하는데 뭘로 조회하는지 -> email 로 오버라이드
        user.modifyRefreshToken(newRefreshToken);
        return newRefreshToken;
    }

    public String reIssueAccessTokenByRefreshToken(HttpServletResponse response, String refreshToken) {
        String email = getUserPk(refreshToken, true);
        return createAccessToken(email);
    }
}