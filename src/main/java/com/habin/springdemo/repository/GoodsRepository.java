package com.habin.springdemo.repository;

import com.habin.springdemo.entity.Goods;
import com.habin.springdemo.repository.querydsl.QGoodsRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsRepository extends JpaRepository<Goods, String>, QGoodsRepository {

}