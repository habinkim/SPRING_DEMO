package com.habin.springdemo.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.io.Serializable;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("AccessToken")
public class AccessToken implements Serializable {

	@Id
	private String userId;

	private String accessToken;

}
