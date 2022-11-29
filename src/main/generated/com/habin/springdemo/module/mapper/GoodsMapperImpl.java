package com.habin.springdemo.module.mapper;

import com.habin.springdemo.dto.goods.GoodsCreateRequestDto;
import com.habin.springdemo.dto.goods.GoodsUpdateRequestDto;
import com.habin.springdemo.entity.Goods;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-11-29T20:47:31+0900",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.5.1.jar, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class GoodsMapperImpl implements GoodsMapper {

    @Override
    public GoodsCreateRequestDto toDto(Goods entity) {
        if ( entity == null ) {
            return null;
        }

        GoodsCreateRequestDto.GoodsCreateRequestDtoBuilder goodsCreateRequestDto = GoodsCreateRequestDto.builder();

        if ( entity.getId() != null ) {
            goodsCreateRequestDto.id( entity.getId() );
        }
        if ( entity.getName() != null ) {
            goodsCreateRequestDto.name( entity.getName() );
        }
        if ( entity.getMfDtm() != null ) {
            goodsCreateRequestDto.mfDtm( entity.getMfDtm() );
        }
        if ( entity.getRemark() != null ) {
            goodsCreateRequestDto.remark( entity.getRemark() );
        }

        return goodsCreateRequestDto.build();
    }

    @Override
    public Goods toEntity(GoodsCreateRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Goods.GoodsBuilder<?, ?> goods = Goods.builder();

        if ( dto.getId() != null ) {
            goods.id( dto.getId() );
        }
        if ( dto.getName() != null ) {
            goods.name( dto.getName() );
        }
        if ( dto.getRemark() != null ) {
            goods.remark( dto.getRemark() );
        }
        if ( dto.getMfDtm() != null ) {
            goods.mfDtm( dto.getMfDtm() );
        }

        return goods.build();
    }

    @Override
    public Goods.GoodsBuilder<?, ?> updateEntityFromDto(GoodsUpdateRequestDto requestDto, Goods.GoodsBuilder<?, ?> toBuilder) {
        if ( requestDto == null ) {
            return toBuilder;
        }

        if ( requestDto.getId() != null ) {
            toBuilder.id( requestDto.getId() );
        }
        if ( requestDto.getName() != null ) {
            toBuilder.name( requestDto.getName() );
        }
        if ( requestDto.getRemark() != null ) {
            toBuilder.remark( requestDto.getRemark() );
        }

        return toBuilder;
    }
}
