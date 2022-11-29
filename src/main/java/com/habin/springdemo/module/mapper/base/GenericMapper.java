package com.habin.springdemo.module.mapper.base;

public interface GenericMapper<E, D> {

	D toDto(E entity);

	E toEntity(D dto);

}
