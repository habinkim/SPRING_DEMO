package com.habin.springdemo.module.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER;
import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;
import static java.time.LocalDate.now;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI api() {
		return new OpenAPI()
				.components(new Components().addSecuritySchemes("TOKEN", securityScheme()))
				.info(apiInfo()); // API 문서에 대한 정보 추가
	}

	private Info apiInfo() {
		return new Info()
				.title("Goods REST API Documentation (" + now() + ")")
				.description("Goods Backend REST API 명세서")
				.version("1.0");
	}

	@Bean
	public SecurityScheme securityScheme() {
		return new SecurityScheme()
				.type(HTTP)
				.scheme("bearer")
				.bearerFormat("JWT")
				.in(HEADER)
				.name("TOKEN");
	}

	@Bean
	public OpenApiCustomiser openAPICustomiser() {
		return openApi -> {
			Map<String, Schema> schemas = openApi.getComponents().getSchemas();
			schemas.values().forEach(schema -> {
				Map<String, Schema<?>> properties = schema.getProperties() != null ?
						schema.getProperties() : Map.of();

				for (String propertyName : properties.keySet()) {
					Schema<?> propertySchema = properties.get(propertyName);
					if (propertySchema instanceof DateTimeSchema) {
						properties.replace(propertyName, new StringSchema()
								.example(propertySchema.getTitle())
								.pattern("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")
								//copies original description
								.description(propertySchema.getDescription()));
					}
				}
			});
		};
	}

}
