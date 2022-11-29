package com.habin.springdemo.module.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habin.springdemo.entity.enums.Role;
import com.habin.springdemo.entity.redis.AccessToken;
import com.habin.springdemo.entity.redis.RefreshToken;
import com.habin.springdemo.module.yml.JwtProperty;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.regex.Pattern;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider { // JWT 토큰 생성 및 검증 모듈

	private Key secretKey;
	private final JwtProperty jwtProperty;
	private final UserDetailsService userDetailsService;
	@Autowired
	@Qualifier("access")
	private RedisTemplate<String, Object> redisTemplateAccess;
	@Autowired
	@Qualifier("refresh")
	private RedisTemplate<String, Object> redisTemplateRefresh;

	private final ObjectMapper objectMapper;

	@PostConstruct
	protected void init() {
		this.secretKey = hmacShaKeyFor(jwtProperty.getSecret().getBytes(UTF_8));
	}

	// JWT 토큰에서 회원 구별 정보 추출
	public String getUserPkFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws ExpiredJwtException {
		final Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
		return claimsResolver.apply(claims);
	}

	public Boolean isAccessTokenExpired(String accessToken) {

		try {
			final Date expiration = getClaimFromToken(accessToken, Claims::getExpiration); // 토큰 만료 여부 체크
			Long expireTime = redisTemplateAccess.getExpire(accessToken); // 로그아웃하면서 만료되었는지 체크
			Boolean result = expiration.before(new Date()) || expireTime >= 0;
			return result;
		} catch (ExpiredJwtException e) {
			return true;
		}

	}

	public Boolean isRefreshTokenExpired(String refreshToken) {

		try {
			final Date expiration = getClaimFromToken(refreshToken, Claims::getExpiration); // 토큰 만료 여부 체크
			Long expireTime = redisTemplateRefresh.getExpire(refreshToken); // 로그아웃하면서 만료되었는지 체크
			Boolean result = expiration.before(new Date()) || expireTime >= 0;
			return result;
		} catch (ExpiredJwtException e) {
			return true;
		}

	}

	// JWT ACCESS 토큰 생성
	public AccessToken createAccessToken(String userPk) {

		String accessTokenKey = new StringBuilder().append("access_").append(userPk).toString();

		Claims claims = Jwts.claims();
		claims.put("role", Role.USER);

		ValueOperations<String, Object> opsForValue = redisTemplateAccess.opsForValue();

		Date now = new Date();
		Date expireDate = new Date(now.getTime() + jwtProperty.getAccessTokenValidity());
		String tokenValue = Jwts.builder()
				.setClaims(claims) // 데이터
				.setSubject(userPk)
				.setIssuedAt(now) // 토큰 발행일자
				.setExpiration(expireDate) // set ExpireTime
				.signWith(secretKey, HS512) // 암호화 알고리즘, secret값 세팅
				.compact();

		AccessToken buildAccessToken = AccessToken.builder().userId(userPk).accessToken(tokenValue).build();

		opsForValue.set(accessTokenKey, buildAccessToken);
		redisTemplateAccess.expireAt(accessTokenKey, expireDate);

		return buildAccessToken;

	}

	public RefreshToken createRefreshToken(String userPk) {

		String refreshTokenKey = new StringBuilder().append("refresh_").append(userPk).toString();

		Claims claims = Jwts.claims();
		claims.put("role", Role.USER);

		ValueOperations<String, Object> opsForValue = redisTemplateRefresh.opsForValue();

		Date now = new Date();
		Date expireDate = new Date(now.getTime() + jwtProperty.getRefreshTokenValidity());
		String tokenValue = Jwts.builder()
				.setClaims(claims) // 데이터
				.setSubject(userPk)
				.setIssuedAt(now) // 토큰 발행일자
				.setExpiration(expireDate) // set ExpireTime
				.signWith(secretKey, HS512) // 암호화 알고리즘, secret값 세팅
				.compact();

		RefreshToken buildRefreshToken = RefreshToken.builder().userId(userPk).refreshToken(tokenValue).build();

		opsForValue.set(refreshTokenKey, buildRefreshToken);
		redisTemplateRefresh.expireAt(refreshTokenKey, expireDate);

		return buildRefreshToken;

	}

	// JWT 토큰으로 인증 정보를 조회
	public Authentication getAuthentication(String token) throws JsonProcessingException {
		UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPkFromToken(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}


	// Request의 Header에서 token 파싱 : "TOKEN: jwt 토큰"
	public String resolveToken(HttpServletRequest req) throws NoSuchElementException {
		String requestHeaderValue = req.getHeader(AUTHORIZATION);

		if(requestHeaderValue == null) {
			throw new NoSuchElementException("RequestHeader에 accessToken이 없습니다.");
		}

		if (Pattern.matches("^Bearer .*", requestHeaderValue)) {
			return requestHeaderValue.substring(7);
		}

		return null;
	}

}
