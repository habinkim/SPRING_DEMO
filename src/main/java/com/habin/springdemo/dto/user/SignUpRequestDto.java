package com.habin.springdemo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {

	/**
	 * 사용자ID
	 */
	@NotBlank(message = "사용자 ID를 입력하지 않으셨습니다.")
	@Pattern(message = "사용자 ID 형식이 올바르지 않습니다.\n(영문자, 숫자 각각 1개 이상)\n(영문자, 숫자 포함 8 ~ 자리)",
			regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
	private String id;

	/**
	 * 패스워드
	 */
	@NotBlank(message = "비밀번호를 입력하지 않으셨습니다.")
	@Pattern(message = "비밀번호 형식이 올바르지 않습니다.\n(영문자, 숫자, 특수문자 각각 1개 이상)\n(대소문자 구분 영문자, 숫자, 특수문자 포함 8 ~ 20자리)",
			regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]{8,20}$")
	private String password;

	/**
	 * 이름
	 */
	@NotBlank(message = "사용자 이름을 입력하지 않으셨습니다.")
	@Size(min = 2, max = 5, message = "사용자 이름 형식이 올바르지 않습니다.(2 ~ 5자리)")
	private String name;

	/**
	 * 상세설명
	 */
	private String remark;

}
