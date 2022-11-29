package com.habin.springdemo.dto.goods;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoodsDeleteRequestDto {

	@NotEmpty(message = "삭제할 상품의 id는 필수 입력값입니다.")
	@NotNull(message = "삭제할 상품의 id는 필수 입력값입니다.")
	private List<String> id;

}
