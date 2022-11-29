package com.habin.springdemo.repository.querydsl.impl;

import com.habin.springdemo.repository.querydsl.QUserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

public class QUserRepositoryImpl implements QUserRepository {
	private final JPAQueryFactory queryFactory;
	
	public QUserRepositoryImpl(EntityManager em) {
	    this.queryFactory = new JPAQueryFactory(em);
	}
}
