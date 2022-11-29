package com.habin.springdemo.module.jpa;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.stream.Collectors.toMap;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PredicateTemplate {

	private final List<Predicate> predicateBuilders = new ArrayList<>();

	public static PredicateTemplate builder() {
		return new PredicateTemplate();
	}

	public <P extends Predicate> PredicateTemplate and(P pr) {
		predicateBuilders.add(pr);
		return this;
	}

	public Predicate build() {
		return ExpressionUtils.allOf(predicateBuilders);
	}

	// StringPath : QueryDSL에서 다루는 Q-Entity 클래스의 String 타입 필드를 나타내는 클래스

	public PredicateTemplate eqString(StringPath column, String value) {

		// column.eq(value) -> BooleanExpression 클래스(Predicate 인터페이스의 구현체 중 하나)

		if (StringUtils.hasText(value)) {
			predicateBuilders.add(column.eq(value));
		}

		return this;
	}

	public PredicateTemplate containsString(StringPath column, String value) {

		if (StringUtils.hasText(value)) {
			predicateBuilders.add(column.contains(value));
		}

		return this;
	}

	public PredicateTemplate containsStringDesc(StringPath column1, StringPath column2, String value) {

		if (StringUtils.hasText(value)) {
			predicateBuilders.add(new BooleanBuilder()
					.andAnyOf(column2.contains(value), column2.contains(value)));
		}

		return this;
	}

	public PredicateTemplate inString(StringPath column, List<String> value) {

		if (value != null) {
			predicateBuilders.add(column.in(value));
		}

		return this;
	}

	public PredicateTemplate eqLong(NumberPath<Long> column, Long value) {

		if (value != null) {
			predicateBuilders.add(column.eq(value));
		}

		return this;
	}

	public PredicateTemplate neLong(NumberPath<Long> column, Long value) {

		if (value != null) {
			predicateBuilders.add(column.ne(value));
		}

		return this;
	}

	public PredicateTemplate inLong(NumberPath<Long> column, List<Long> value) {

		if (value != null) {
			predicateBuilders.add(column.in(value));
		}

		return this;
	}

	public <E extends Enum<E>> PredicateTemplate eqEnum(EnumPath<E> column, E value) {

		if (value != null) {
			predicateBuilders.add(column.eq(value));
		}

		return this;
	}

	public <E extends Enum<E>> PredicateTemplate inEnum(EnumPath<E> column, List<E> value) {

		if (value != null) {
			predicateBuilders.add(column.in(value));
		}

		return this;
	}

	public PredicateTemplate betweenDateTime(DateTimePath<LocalDateTime> column, LocalDateTime startDate, LocalDateTime endDate) {

		if (startDate != null && endDate != null) {
			predicateBuilders.add(column.between(startDate, endDate));
		}

		return this;
	}

	public PredicateTemplate betweenDateTimeDynamic(DateTimePath<LocalDateTime> column, LocalDateTime startDate, LocalDateTime endDate) {
		if (startDate != null) {
			predicateBuilders.add(column.goe(startDate));
		}

		if (endDate != null) {
			predicateBuilders.add(column.loe(endDate));
		}

		return this;
	}

	public PredicateTemplate betweenDate(DatePath<LocalDate> column, LocalDate startDate, LocalDate endDate) {
		if (startDate != null && endDate != null) {
			predicateBuilders.add(column.between(startDate, endDate));
		}

		return this;
	}

	public PredicateTemplate betweenDateDynamic(DatePath<LocalDate> column, LocalDate startDate, LocalDate endDate) {
		if (startDate != null) {
			predicateBuilders.add(column.goe(startDate));
		}

		if (endDate != null) {
			predicateBuilders.add(column.loe(endDate));
		}

		return this;
	}

	public PredicateTemplate eqDateTime(DateTimePath<LocalDateTime> column, LocalDateTime value) {

		if (value != null) {
			predicateBuilders.add(column.eq(value));
		}

		return this;
	}

	public PredicateTemplate eqDate(DatePath<LocalDate> column, LocalDate value) {

		if (value != null) {
			predicateBuilders.add(column.eq(value));
		}

		return this;
	}

	public static <QE extends EntityPathBase<E>, E> void DynamicUpdate(E Entity, QE QEntity, JPAQueryFactory queryFactory) throws IllegalAccessException {

		PathBuilder<E> QEntityPath = new PathBuilder<>(QEntity.getType(), QEntity.getMetadata());

		Class<E> entityClass = (Class<E>) Entity.getClass();
		List<Field> fields = Arrays.asList(entityClass.getDeclaredFields());
		fields.forEach(f -> f.setAccessible(true));

		AtomicReference<Field> idFieldRefer = null;

		Map<PathBuilder<?>, Object> collect = fields.stream()
				.filter(f -> {
//				f.setAccessible(true);
					try {
						if (f.getDeclaredAnnotation(Id.class) != null) {
							idFieldRefer.set(f);
						}
						return f.getDeclaredAnnotation(Id.class) == null && f.get(Entity) != null;
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
					return false;
				})
				.collect(toMap(
						f -> QEntityPath.get(f.getName()),
						f -> {
							try {
								return f.get(Entity);
							} catch (IllegalArgumentException | IllegalAccessException e) {
								e.printStackTrace();
							}
							return f;
						}
				));

		Field idField = idFieldRefer.get();

		queryFactory.update(QEntityPath)
				.where(QEntityPath.get(idField.getName()).eq(idField.get(entityClass)))
				.set(new ArrayList<>(collect.keySet()), new ArrayList<>(collect.values()))
				.execute();

	}

}
