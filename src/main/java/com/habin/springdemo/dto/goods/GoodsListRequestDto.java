package com.habin.springdemo.dto.goods;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.habin.springdemo.module.json.CustomLocalDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoodsListRequestDto {

	/**
	 * 제품ID
	 */
	private String id;

	/**
	 * 제품명
	 */
	private String name;

	/**
	 * 제조일자 시작일
	 */
	@PastOrPresent(message = "과거 일자만 입력이 가능합니다.")
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime mfStartDate;

	/**
	 * 제조일자 종료일
	 */
	@PastOrPresent(message = "과거 일자만 입력이 가능합니다.")
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime mfEndDate;

	@Builder.Default
	@Positive(message = "페이지 번호는 양수이어야 합니다.")
	@Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
	private Integer pageNo = 1;

	@Builder.Default
	@Positive(message = "페이지 사이즈는 양수이어야 합니다.")
	@Min(value = 2, message = "페이지 사이즈는 2 이상이어야 합니다.")
	private Integer pageSize = 10;

}
