package com.habin.springdemo.entity.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Role implements GrantedAuthority {

	ADMIN(ROLES.ADMIN),
	MANAGER(ROLES.MANAGER),
	USER(ROLES.USER);

	public static class ROLES {
		public static final String ADMIN = "ROLE_ADMIN";
		public static final String MANAGER = "ROLE_MANAGER";
		public static final String USER = "ROLE_USER";
	}

	private String value;

	@Override
	public String getAuthority() {
		return value;
	}
}
