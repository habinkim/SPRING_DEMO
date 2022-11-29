package com.habin.springdemo.module.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habin.springdemo.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author : 김하빈(hbkim@bpnsolution.com)
 * @description : Jwt 인증 진입점 클래스
 * @Date : 2020. 10. 7.
 * @Time : 오전 9:09:52
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

	private static final ResponseDto exceptionResponse =
			new ResponseDto("3", "권한이 없습니다. 로그인해주세요.");

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
	                     AuthenticationException authException) throws IOException, ServletException, InsufficientAuthenticationException {
		log.error("Unauthorized error: {}", authException.getMessage());
		log.error("", authException);

		response.setCharacterEncoding("UTF-8");
		response.setContentType(APPLICATION_JSON_VALUE + ";charset=UTF-8");
		response.setStatus(SC_UNAUTHORIZED);
		try (OutputStream os = response.getOutputStream()) {
			ObjectMapper om = new ObjectMapper();
			om.writeValue(os, exceptionResponse);
			os.flush();
		}
	}
}
