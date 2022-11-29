package com.habin.springdemo.module.config;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {

	@Value("${jasypt.encryptor.password}")
	private String encryptKey;

	@Bean("jasyptStringEncryptor")
	public StringEncryptor stringEncryptor() {
		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();

		config.setPassword(encryptKey);
		encryptor.setProvider(new BouncyCastleProvider());
		encryptor.setAlgorithm("PBEWITHSHA256AND128BITAES-CBC-BC");
		config.setKeyObtentionIterations("1000");
		config.setPoolSize("1");
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
		config.setStringOutputType("base64");
		encryptor.setConfig(config);

		return encryptor;
	}

}
