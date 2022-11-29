package com.habin.springdemo.module.mapper;

import com.habin.springdemo.dto.user.SignUpRequestDto;
import com.habin.springdemo.entity.User;
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
public interface UserMapper extends GenericMapper<User, SignUpRequestDto> {



}
