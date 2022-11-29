package com.habin.springdemo.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("RefreshToken")
public class RefreshToken implements Serializable {

	private static final long serialVersionUID = 7882162987576256776L;

	@Id
	private String userId;

	private String refreshToken;

}
