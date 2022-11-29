package com.habin.springdemo.repository.querydsl.impl;

import com.habin.springdemo.dto.goods.GoodsListRequestDto;
import com.habin.springdemo.dto.goods.GoodsListResponseDto;
import com.habin.springdemo.module.jpa.PredicateTemplate;
import com.habin.springdemo.repository.querydsl.QGoodsRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static com.habin.springdemo.entity.QGoods.goods;
import static com.querydsl.core.types.Projections.fields;
import static org.springframework.data.support.PageableExecutionUtils.getPage;

public class QGoodsRepositoryImpl implements QGoodsRepository {

	private final JPAQueryFactory queryFactory;

	public QGoodsRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Page<GoodsListResponseDto> getGoodsList(GoodsListRequestDto requestDto) {
		Pageable pageable = PageRequest.of(requestDto.getPageNo() - 1, requestDto.getPageSize());

		Predicate predicate = PredicateTemplate.builder()
				.containsString(goods.id, requestDto.getId())
				.containsString(goods.name, requestDto.getName())
				.betweenDateTimeDynamic(goods.mfDtm, requestDto.getMfStartDate(), requestDto.getMfEndDate())
				.build();

		List<GoodsListResponseDto> fetch = queryFactory.select(
						fields(GoodsListResponseDto.class,
								goods.id,
								goods.name,
								goods.mfDtm,
								goods.insDtm
						)
				)
				.from(goods)
				.where(predicate)
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		List<String> countFetch = queryFactory.select(goods.id)
				.from(goods)
				.where(predicate)
				.fetch();

		return getPage(fetch, pageable, countFetch::size);

	}
}
