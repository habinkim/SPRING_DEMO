package com.habin.springdemo.service;

import com.habin.springdemo.dto.ResponseDto;
import com.habin.springdemo.dto.goods.*;
import com.habin.springdemo.entity.Goods;
import com.habin.springdemo.module.mapper.GoodsMapper;
import com.habin.springdemo.repository.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.habin.springdemo.module.singleton.StaticLambdaExpression.getNSEE;


@Service
@Transactional
@RequiredArgsConstructor
public class GoodsService {

	private final GoodsRepository goodsRepository;
	private final GoodsMapper goodsMapper;

	@Transactional(readOnly = true)
	public ResponseEntity<ResponseDto> getGoodsList(GoodsListRequestDto requestDto) {
		Page<GoodsListResponseDto> list = goodsRepository.getGoodsList(requestDto);
		return new ResponseDto(list, "제품 리스트 조회에 성공했습니다.").wrap();
	}

	@Transactional(readOnly = true)
	public ResponseEntity<ResponseDto> getGoodsInfo(String id) {
		Goods goods = goodsRepository.findById(id).orElseThrow(getNSEE.apply("제품 정보"));
		return new ResponseDto(goods, "제품 정보 상세 조회에 성공했습니다.").wrap();
	}

	@Transactional
	public ResponseEntity<ResponseDto> createGoods(GoodsCreateRequestDto requestDto) {
		Goods goods = goodsMapper.toEntity(requestDto);
		goodsRepository.save(goods);

		return new ResponseDto("제품 생성에 성공했습니다.").wrap();
	}

	@Transactional
	public ResponseEntity<ResponseDto> updateGoods(GoodsUpdateRequestDto requestDto) {
		Goods goods = goodsRepository.findById(requestDto.getId()).orElseThrow(getNSEE.apply("제품 정보"));
		goods = goodsMapper.updateEntityFromDto(requestDto, goods.toBuilder()).build();
		goodsRepository.save(goods);

		return new ResponseDto("제품 정보 수정에 성공했습니다.").wrap();
	}

	@Transactional
	public ResponseEntity<ResponseDto> deleteGoods(GoodsDeleteRequestDto requestDto) {
		goodsRepository.deleteAllById(requestDto.getId());
		return new ResponseDto("제품 정보 삭제에 성공했습니다.").wrap();
	}
}
