package com.habin.springdemo;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.habin.springdemo.module.initializer.EncryptInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SpringDemoApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(SpringDemoApplication.class).initializers(new EncryptInitializer()).run(args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	Hibernate5Module hibernate5Module() {
		return new Hibernate5Module();
	}

}
