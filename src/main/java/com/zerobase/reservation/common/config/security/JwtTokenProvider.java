package com.zerobase.reservation.common.config.security;

import com.zerobase.reservation.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${springboot.jwt.secret}")
    private String secretKey = "secretKey";

    private final long validityInMilliseconds = 1000L * 60 * 60; // 토큰 유효시간(ms)
    private MemberService memberService;

    @PostConstruct
    protected void init() {
        log.info("JwtTokenProvider 내 secretKey 초기화");
        secretKey = Base64.getEncoder()
                          .encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String username) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                   .setSubject(username)
                   .setIssuedAt(now)
                   .setExpiration(validity)
                   .signWith(SignatureAlgorithm.HS256, secretKey)
                   .compact();
    }

    public Authentication getAuthentication(String token) {
        log.info("토큰 인증 정보 조회");
        UserDetails userDetails = memberService.loadUserByUsername(this.getUsernameFromToken(token));
        log.info("토큰 인증 정보 조회 완료, MemberName: {}", userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsernameFromToken(String token) {
        String info = Jwts.parser()
                          .setSigningKey(secretKey)
                          .parseClaimsJws(token)
                          .getBody()
                          .getSubject();

        log.info("토큰 기반 회원 정보 추출, info: {}", info);
        return info;
    }

    public String resolveToken(HttpServletRequest request) {
        log.info("[resolveToken] Http 헤더에서 Token값 추출");
        return request.getHeader("X-AUTH-TOKEN");
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                                     .setSigningKey(secretKey)
                                     .parseClaimsJws(token);
            return !claims.getBody()
                          .getExpiration()
                          .before(new Date());
        } catch (Exception e) {
            log.info("토큰 유효 체크 예외 발생");
            return false;
        }
    }
}
