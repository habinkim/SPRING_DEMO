package com.habin.springdemo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.habin.springdemo.dto.ResponseDto;
import com.habin.springdemo.dto.user.LoginRequestDto;
import com.habin.springdemo.dto.user.RefreshDto;
import com.habin.springdemo.dto.user.SignUpRequestDto;
import com.habin.springdemo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "로그인 관리", description = "로그인 관련 API")
@RestController
@RequiredArgsConstructor
public class HomeController {

	private final UserService userService;

	@Tag(name = "로그인 관리", description = "로그인 관련 API")
	@Operation(summary = "회원가입", description = "회원가입 API")
	@PostMapping("/signUp")
	public ResponseEntity<ResponseDto> signUp(@Valid @RequestBody SignUpRequestDto requestDto) {
		return userService.signUp(requestDto);
	}

	@Tag(name = "로그인 관리", description = "로그인 관련 API")
	@Operation(summary = "로그인", description = "로그인 API")
	@PostMapping("/login")
	public ResponseEntity<ResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
		return userService.login(requestDto);
	}

	@Tag(name = "로그인 관리", description = "로그인 관련 API")
	@Operation(summary = "로그아웃", description = "로그아웃 API")
	@SecurityRequirement(name = "TOKEN")
	@GetMapping("/logoutUser")
	public ResponseEntity<ResponseDto> logout() {
		return userService.logout();
	}

	@Tag(name = "로그인 관리", description = "로그인 관련 API")
	@Operation(summary = "accessToken 재생성", description = "accessToken 재생성 API")
	@PostMapping("/refresh")
	public ResponseEntity<ResponseDto> refresh(@RequestBody RefreshDto refreshDto) {
		return userService.refresh(refreshDto.getRefreshToken());
	}

	@Tag(name = "로그인 관리", description = "로그인 관련 API")
	@Operation(summary = "자동 로그인 처리", description = "자동 로그인 처리 API")
	@SecurityRequirement(name = "TOKEN")
	@GetMapping("/login/auto")
	public ResponseEntity<ResponseDto> autoLogin() throws JsonProcessingException {
		return userService.autoLogin();
	}

}
