package com.habin.springdemo.repository.querydsl;

import com.habin.springdemo.dto.goods.GoodsListRequestDto;
import com.habin.springdemo.dto.goods.GoodsListResponseDto;
import org.springframework.data.domain.Page;

public interface QGoodsRepository {

	Page<GoodsListResponseDto> getGoodsList(GoodsListRequestDto requestDto);
}
