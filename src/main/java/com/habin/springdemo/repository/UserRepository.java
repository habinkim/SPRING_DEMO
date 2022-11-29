package com.habin.springdemo.repository;

import com.habin.springdemo.entity.User;
import com.habin.springdemo.repository.querydsl.QUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String>, QUserRepository {

}