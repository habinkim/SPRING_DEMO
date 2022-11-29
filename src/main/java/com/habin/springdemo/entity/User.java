package com.habin.springdemo.entity;

import com.habin.springdemo.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Collection;

import static java.util.Collections.singletonList;

/**
 * 사용자 정보
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "EDU_USER")
public class User implements Serializable, UserDetails {

	private static final long serialVersionUID = 1L;

	/**
	 * 사용자ID
	 */
	@Id
	@Column(name = "ID", nullable = false)
	private String id;

	/**
	 * 패스워드
	 */
	@Column(name = "PASSWORD")
	private String password;

	/**
	 * 이름
	 */
	@Column(name = "NAME")
	private String name;

	/**
	 * 상세설명
	 */
	@Column(name = "REMARK")
	private String remark;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return singletonList(Role.USER);
	}

	@Override
	public String getUsername() {
		return id;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}
}
