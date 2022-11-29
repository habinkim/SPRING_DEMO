package com.habin.springdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habin.springdemo.dto.ResponseDto;
import com.habin.springdemo.dto.user.LoginRequestDto;
import com.habin.springdemo.dto.user.SignUpRequestDto;
import com.habin.springdemo.entity.User;
import com.habin.springdemo.entity.redis.AccessToken;
import com.habin.springdemo.entity.redis.RefreshToken;
import com.habin.springdemo.module.mapper.UserMapper;
import com.habin.springdemo.module.security.JwtTokenProvider;
import com.habin.springdemo.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.habin.springdemo.module.singleton.StaticLambdaExpression.getNSEE;
import static java.util.concurrent.TimeUnit.HOURS;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final ObjectMapper objectMapper;
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private final JwtTokenProvider jwtTokenProvider;

	@Autowired
	@Qualifier("access")
	private RedisTemplate<String, Object> redisTemplateAccess;

	@Autowired
	@Qualifier("refresh")
	private RedisTemplate<String, Object> redisTemplateRefresh;


	@Transactional
	public ResponseEntity<ResponseDto> signUp(SignUpRequestDto requestDto) {
		Optional<User> byId = userRepository.findById(requestDto.getId());
		if (byId.isPresent()) {
			return new ResponseDto("2", "이미 가입된 아이디입니다.").wrap();
		}

		requestDto = requestDto.toBuilder().password(passwordEncoder.encode(requestDto.getPassword())).build();

		User user = userMapper.toEntity(requestDto);
		userRepository.save(user);

		return new ResponseDto("회원가입에 성공했습니다.").wrap();
	}

	@Transactional(readOnly = true)
	public ResponseEntity<ResponseDto> login(LoginRequestDto requestDto) {

		User user = userRepository.findById(requestDto.getId()).orElseThrow(() -> new NoSuchElementException("가입되지 않은 아이디입니다."));

		Boolean pwIsMatch = passwordEncoder.matches(requestDto.getPassword(), user.getPassword());

		if (pwIsMatch) {

			AccessToken accessToken = jwtTokenProvider.createAccessToken(requestDto.getId());
			RefreshToken refreshToken = jwtTokenProvider.createRefreshToken(requestDto.getId());

			Map<String, Object> result = new HashMap<>();
			result.put("accessToken", accessToken);
			result.put("refreshToken", refreshToken);

			String loginMsg = "안녕하세요. " + user.getId() + "님.";

			return  new ResponseDto(result, loginMsg).wrap();
		} else {
			return new ResponseDto("2", "비밀번호가 일치하지 않습니다.").wrap();
		}

	}

	@Transactional(readOnly = true)
	public ResponseEntity<ResponseDto> logout() {
		HttpServletRequest request = ((ServletRequestAttributes) currentRequestAttributes()).getRequest();
		ResponseEntity<ResponseDto> res = new ResponseDto("로그아웃되었습니다.").wrap();
		ValueOperations<String, Object> opsForValue = redisTemplateAccess.opsForValue();
		ValueOperations<String, Object> opsForValue2 = redisTemplateRefresh.opsForValue();

		String userId = null;
		String accessToken = jwtTokenProvider.resolveToken(request);

		try {
			userId = jwtTokenProvider.getUserPkFromToken(accessToken); // access_token에서 user_id를 가져옴(유효성 검사)
		} catch (IllegalArgumentException ignored) {
		} catch (ExpiredJwtException e) { // 만료
			userId = e.getClaims().getSubject(); // 만료된 access token으로부터 user_id를 가져옴
			log.info("user_id from expired access token : " + userId);
		}

		if (opsForValue.get("access_" + userId) != null) {
			redisTemplateAccess.delete("access_" + userId);
			opsForValue.set(accessToken, true);
			redisTemplateAccess.expire(accessToken, 1, HOURS);
		}

		try {
			Object refreshTokenObject = opsForValue2.get("refresh_" + userId);
			if (refreshTokenObject != null) { // refresh token이 db에 저장되어있을때
				redisTemplateAccess.delete("refresh_" + userId); // refresh token의 데이터를 삭제
			}
		} catch (IllegalArgumentException e) {
			log.warn("user does not exist");
		}

		log.info(" logout ing : " + accessToken);

		return res;
	}

	@Transactional(readOnly = true)
	public ResponseEntity<ResponseDto> refresh(String refreshToken) {
		ResponseEntity<ResponseDto> res = new ResponseEntity<>(HttpStatus.OK);
		String userId = null;

		if (!jwtTokenProvider.isRefreshTokenExpired(refreshToken)) { // refresh token 만료되지 않았을때 -> 여기서 예외가 터짐
			userId = jwtTokenProvider.getUserPkFromToken(refreshToken); // access_token에서 user_id 가져옴(유효성 검사)
			User user = userRepository.findById(userId).orElseThrow(getNSEE.apply("사용자 정보"));

			AccessToken newAccessToken = jwtTokenProvider.createAccessToken(user.getId());

			res = new ResponseDto(newAccessToken, "accessToken 재발급 처리에 성공했습니다.").wrap();
		} else { // refresh 토큰 expire
			res = new ResponseDto("2", "refreshToken이 만료되었습니다. 다시 로그인해주세요.", UNAUTHORIZED).wrap();
		}

		return res;
	}

	public ResponseEntity<ResponseDto> autoLogin() throws JsonProcessingException {
		HttpServletRequest request = ((ServletRequestAttributes) currentRequestAttributes()).getRequest();
		String accessToken = jwtTokenProvider.resolveToken(request);
		Map<String, Object> data = new HashMap<>();

		if(jwtTokenProvider.isAccessTokenExpired(accessToken)) {
//			accessToken = objectMapper.readValue(objectMapper.writeValueAsString(refresh(accessToken).getBody().getData()),
//					new TypeReference<>() {});
			return new ResponseDto("2", "accessToken이 만료되었습니다. accessToken 재발급을 진행해주세요.", FORBIDDEN).wrap();
		}

		String userId = jwtTokenProvider.getUserPkFromToken(accessToken);

		ValueOperations<String, Object> opsForValue = redisTemplateRefresh.opsForValue();
		Object refreshTokenObj = opsForValue.get("refresh_" + userId);
		RefreshToken savedRefreshToken = objectMapper.readValue(objectMapper.writeValueAsString(refreshTokenObj),
				new TypeReference<>() {});

		data.put("accessToken", accessToken);
		data.put("refreshToken", savedRefreshToken);

		return new ResponseDto(data, "자동 로그인 처리에 성공했습니다.").wrap();
	}

}
