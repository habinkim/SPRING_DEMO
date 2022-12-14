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
			return new ResponseDto("2", "?????? ????????? ??????????????????.").wrap();
		}

		requestDto = requestDto.toBuilder().password(passwordEncoder.encode(requestDto.getPassword())).build();

		User user = userMapper.toEntity(requestDto);
		userRepository.save(user);

		return new ResponseDto("??????????????? ??????????????????.").wrap();
	}

	@Transactional(readOnly = true)
	public ResponseEntity<ResponseDto> login(LoginRequestDto requestDto) {

		User user = userRepository.findById(requestDto.getId()).orElseThrow(() -> new NoSuchElementException("???????????? ?????? ??????????????????."));

		Boolean pwIsMatch = passwordEncoder.matches(requestDto.getPassword(), user.getPassword());

		if (pwIsMatch) {

			AccessToken accessToken = jwtTokenProvider.createAccessToken(requestDto.getId());
			RefreshToken refreshToken = jwtTokenProvider.createRefreshToken(requestDto.getId());

			Map<String, Object> result = new HashMap<>();
			result.put("accessToken", accessToken);
			result.put("refreshToken", refreshToken);

			String loginMsg = "???????????????. " + user.getId() + "???.";

			return  new ResponseDto(result, loginMsg).wrap();
		} else {
			return new ResponseDto("2", "??????????????? ???????????? ????????????.").wrap();
		}

	}

	@Transactional(readOnly = true)
	public ResponseEntity<ResponseDto> logout() {
		HttpServletRequest request = ((ServletRequestAttributes) currentRequestAttributes()).getRequest();
		ResponseEntity<ResponseDto> res = new ResponseDto("???????????????????????????.").wrap();
		ValueOperations<String, Object> opsForValue = redisTemplateAccess.opsForValue();
		ValueOperations<String, Object> opsForValue2 = redisTemplateRefresh.opsForValue();

		String userId = null;
		String accessToken = jwtTokenProvider.resolveToken(request);

		try {
			userId = jwtTokenProvider.getUserPkFromToken(accessToken); // access_token?????? user_id??? ?????????(????????? ??????)
		} catch (IllegalArgumentException ignored) {
		} catch (ExpiredJwtException e) { // ??????
			userId = e.getClaims().getSubject(); // ????????? access token???????????? user_id??? ?????????
			log.info("user_id from expired access token : " + userId);
		}

		if (opsForValue.get("access_" + userId) != null) {
			redisTemplateAccess.delete("access_" + userId);
			opsForValue.set(accessToken, true);
			redisTemplateAccess.expire(accessToken, 1, HOURS);
		}

		try {
			Object refreshTokenObject = opsForValue2.get("refresh_" + userId);
			if (refreshTokenObject != null) { // refresh token??? db??? ?????????????????????
				redisTemplateAccess.delete("refresh_" + userId); // refresh token??? ???????????? ??????
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

		if (!jwtTokenProvider.isRefreshTokenExpired(refreshToken)) { // refresh token ???????????? ???????????? -> ????????? ????????? ??????
			userId = jwtTokenProvider.getUserPkFromToken(refreshToken); // access_token?????? user_id ?????????(????????? ??????)
			User user = userRepository.findById(userId).orElseThrow(getNSEE.apply("????????? ??????"));

			AccessToken newAccessToken = jwtTokenProvider.createAccessToken(user.getId());

			res = new ResponseDto(newAccessToken, "accessToken ????????? ????????? ??????????????????.").wrap();
		} else { // refresh ?????? expire
			res = new ResponseDto("2", "refreshToken??? ?????????????????????. ?????? ?????????????????????.", UNAUTHORIZED).wrap();
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
			return new ResponseDto("2", "accessToken??? ?????????????????????. accessToken ???????????? ??????????????????.", FORBIDDEN).wrap();
		}

		String userId = jwtTokenProvider.getUserPkFromToken(accessToken);

		ValueOperations<String, Object> opsForValue = redisTemplateRefresh.opsForValue();
		Object refreshTokenObj = opsForValue.get("refresh_" + userId);
		RefreshToken savedRefreshToken = objectMapper.readValue(objectMapper.writeValueAsString(refreshTokenObj),
				new TypeReference<>() {});

		data.put("accessToken", accessToken);
		data.put("refreshToken", savedRefreshToken);

		return new ResponseDto(data, "?????? ????????? ????????? ??????????????????.").wrap();
	}

}
