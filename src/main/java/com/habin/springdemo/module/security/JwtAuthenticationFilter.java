package com.habin.springdemo.module.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habin.springdemo.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * @author : 김하빈(hbkim@bpnsolution.com)
 * @description : Jwt 토큰 인증 필터 클래스
 * @Date : 2020. 10. 7.
 * @Time : 오전 9:09:09
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	// Request로 들어오는 JWT Token의 유효성을 검증(jwtTokenProvider.validateToken)하는 filter를 filterChain에 등록
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String token = jwtTokenProvider.resolveToken(request);

		if (token != null && !jwtTokenProvider.isAccessTokenExpired(token)) {
			Authentication auth = jwtTokenProvider.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		if (token != null && jwtTokenProvider.isAccessTokenExpired(token)) {
			ObjectMapper om = new ObjectMapper();
			ResponseDto res = new ResponseDto("2", "accessToken이 만료되었습니다. accessToken 재발급을 진행해주세요.", FORBIDDEN);

			response.setCharacterEncoding("UTF-8");
			response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
			response.setStatus(SC_FORBIDDEN);
			try (OutputStream os = response.getOutputStream()) {
				om.writeValue(os, res);
				os.flush();
			}
		}

		filterChain.doFilter(request, response);
	}

}
