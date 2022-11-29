package com.habin.springdemo.controller;

import com.habin.springdemo.dto.ResponseDto;
import com.habin.springdemo.dto.goods.GoodsCreateRequestDto;
import com.habin.springdemo.dto.goods.GoodsDeleteRequestDto;
import com.habin.springdemo.dto.goods.GoodsListRequestDto;
import com.habin.springdemo.dto.goods.GoodsUpdateRequestDto;
import com.habin.springdemo.service.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "상품 관리", description = "상품 관리 정보 API")
@SecurityRequirement(name = "TOKEN")
@RestController
@RequiredArgsConstructor
@RequestMapping("/goods")
public class GoodsController {

	private final GoodsService goodsService;

	@Tag(name = "상품 관리", description = "상품 관리 정보 API")
	@Operation(summary = "상품 리스트 조회", description = "상품 리스트 조회 API")
	@GetMapping("/list")
	public ResponseEntity<ResponseDto> getGoodsList(@Valid @RequestBody GoodsListRequestDto requestDto) {
		return goodsService.getGoodsList(requestDto);
	}

	@Tag(name = "상품 관리", description = "상품 관리 정보 API")
	@Operation(summary = "상품 상세 조회", description = "상품 상세 조회 API")
	@GetMapping("/info")
	public ResponseEntity<ResponseDto> getGoodsInfo(@RequestParam String id) {
		return goodsService.getGoodsInfo(id);
	}

	@Tag(name = "상품 관리", description = "상품 관리 정보 API")
	@Operation(summary = "상품 생성", description = "상품 생성 API")
	@PostMapping("/create")
	public ResponseEntity<ResponseDto> createGoods(@Valid @RequestBody GoodsCreateRequestDto requestDto) {
		return goodsService.createGoods(requestDto);
	}

	@Tag(name = "상품 관리", description = "상품 관리 정보 API")
	@Operation(summary = "상품 수정", description = "상품 수정 API")
	@PutMapping("/update")
	public ResponseEntity<ResponseDto> updateGoods(@Valid @RequestBody GoodsUpdateRequestDto requestDto) {
		return goodsService.updateGoods(requestDto);
	}

	@Tag(name = "상품 관리", description = "상품 관리 정보 API")
	@Operation(summary = "상품 삭제", description = "상품 삭제 API")
	@DeleteMapping("/delete")
	public ResponseEntity<ResponseDto> deleteGoods(@RequestBody GoodsDeleteRequestDto requestDto) {
		return goodsService.deleteGoods(requestDto);
	}

}
