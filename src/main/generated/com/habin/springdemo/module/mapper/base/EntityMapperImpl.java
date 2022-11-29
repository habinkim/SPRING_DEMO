package com.habin.springdemo.module.mapper.base;

import com.habin.springdemo.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-11-29T20:47:31+0900",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.5.1.jar, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class EntityMapperImpl implements EntityMapper {

    @Autowired
    private ReferenceMapper referenceMapper;

    @Override
    public User idToUser(String id) {
        if ( id == null ) {
            return null;
        }

        User user = referenceMapper.stringToEntity( id, User.class );

        return user;
    }
}
