package com.habin.springdemo.dto.goods;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.habin.springdemo.module.json.CustomLocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoodsListResponseDto {

	/**
	 * 제품ID
	 */
	private String id;

	/**
	 * 제품명
	 */
	private String name;

	/**
	 * 제조일자
	 */
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	private String mfDtm;

	/**
	 * 등록일자
	 */
	private String insDtm;

}
