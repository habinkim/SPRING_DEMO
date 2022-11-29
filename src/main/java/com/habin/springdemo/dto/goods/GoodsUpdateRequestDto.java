package com.habin.springdemo.dto.goods;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoodsUpdateRequestDto {

	/**
	 * 제품ID
	 */
	@NotBlank(message = "제품 ID를 입력하지 않으셨습니다.")
	@Pattern(message = "제품 ID는 영대문자와 숫자 조합의 12자리만 입력하실 수 있습니다.", regexp = "^[A-Z\\d]{12}$")
	private String id;

	/**
	 * 제품명
	 */
	private String name;

	/**
	 * 제조일자
	 */
	@PastOrPresent(message = "과거 일자만 입력이 가능합니다.")
	private LocalDateTime mfDate;

	/**
	 * 상세설명
	 */
	private String remark;

}
