package com.habin.springdemo.module.mapper;

import com.habin.springdemo.dto.goods.GoodsCreateRequestDto;
import com.habin.springdemo.dto.goods.GoodsUpdateRequestDto;
import com.habin.springdemo.entity.Goods;
import com.habin.springdemo.module.mapper.base.EntityMapper;
import com.habin.springdemo.module.mapper.base.GenericMapper;
import org.mapstruct.*;

@Mapper(
		componentModel = "spring",
		uses = {EntityMapper.class},
		unmappedTargetPolicy = ReportingPolicy.IGNORE,
		nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface GoodsMapper extends GenericMapper<Goods, GoodsCreateRequestDto> {


	Goods.GoodsBuilder<?, ?> updateEntityFromDto(GoodsUpdateRequestDto requestDto, @MappingTarget Goods.GoodsBuilder<?,?> toBuilder);

}
