package com.habin.springdemo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

	/**
	 * 사용자ID
	 */
	@NotBlank(message = "사용자 ID를 입력하지 않으셨습니다.")
	private String id;

	/**
	 * 패스워드
	 */
	@NotBlank(message = "패스워드를 입력하지 않으셨습니다.")
	private String password;

}
