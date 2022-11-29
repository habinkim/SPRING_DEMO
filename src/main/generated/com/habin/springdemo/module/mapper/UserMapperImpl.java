package com.habin.springdemo.module.mapper;

import com.habin.springdemo.dto.user.SignUpRequestDto;
import com.habin.springdemo.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-11-29T20:47:31+0900",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.5.1.jar, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public SignUpRequestDto toDto(User entity) {
        if ( entity == null ) {
            return null;
        }

        SignUpRequestDto.SignUpRequestDtoBuilder signUpRequestDto = SignUpRequestDto.builder();

        if ( entity.getId() != null ) {
            signUpRequestDto.id( entity.getId() );
        }
        if ( entity.getPassword() != null ) {
            signUpRequestDto.password( entity.getPassword() );
        }
        if ( entity.getName() != null ) {
            signUpRequestDto.name( entity.getName() );
        }
        if ( entity.getRemark() != null ) {
            signUpRequestDto.remark( entity.getRemark() );
        }

        return signUpRequestDto.build();
    }

    @Override
    public User toEntity(SignUpRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        if ( dto.getId() != null ) {
            user.id( dto.getId() );
        }
        if ( dto.getPassword() != null ) {
            user.password( dto.getPassword() );
        }
        if ( dto.getName() != null ) {
            user.name( dto.getName() );
        }
        if ( dto.getRemark() != null ) {
            user.remark( dto.getRemark() );
        }

        return user.build();
    }
}
