package com.habin.springdemo.module.mapper.base;

import org.mapstruct.ObjectFactory;
import org.mapstruct.TargetType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class ReferenceMapper {

	@PersistenceContext
	private EntityManager entityManager;

	@ObjectFactory
	public <T> T integerToEntity(@NonNull final Integer id, @TargetType Class<T> type) {
		return entityManager.getReference(type, id);
	}

	@ObjectFactory
	public <T> T longToEntity(@NonNull final Long id, @TargetType Class<T> type) {
		return entityManager.getReference(type, id);
	}

	@ObjectFactory
	public <T> T stringToEntity(@NonNull final String id, @TargetType Class<T> type) {
		return entityManager.getReference(type, id);
	}

}
