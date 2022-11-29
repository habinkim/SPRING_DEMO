package com.habin.springdemo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

//@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResponseDto {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Object data; // 결과값

	public String code; // 오류 여부
	public String message; // 메시지

	@JsonIgnore
	public HttpStatus httpStatus; // Http Status Code

	public ResponseDto(Object data, String message) { // 생성자 (조회 성공 처리)
		this.data = data;
		this.code = "1";
		this.message = message;
	}

	public ResponseDto(String code, String message) { // 생성자 (오류 처리)
		this.data = null;
		this.code = code;
		this.message = message;
	}

	public ResponseDto(String message) { // 생성자 (데이터 삭제 성공 처리)
		this.data = null;
		this.code = "1";
		this.message = message;
	}

	public ResponseDto(String message, HttpStatus httpStatus) { // 상태코드
		this.data = null;
		this.code = null;
		this.message = message;
		this.httpStatus = httpStatus;
	}

	public ResponseDto(String exception, String code, String message, HttpStatus httpStatus) { // 상태코드
		this.data = exception;
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}

	public ResponseDto(String code, String message, HttpStatus httpStatus) { // 상태코드
		this.data = null;
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}

	public ResponseDto(String exception, String code, String message) {
		this.data = exception;
		this.code = code;
		this.message = message;
		this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	}

	public ResponseEntity<ResponseDto> wrap() { // ResponseEntity로 wrapping하는 메소드
		return httpStatus != null ? new ResponseEntity<>(this, this.httpStatus) : ResponseEntity.ok(this);
	}

}