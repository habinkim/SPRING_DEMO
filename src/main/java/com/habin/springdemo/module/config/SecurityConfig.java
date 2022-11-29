package com.habin.springdemo.module.config;

import com.habin.springdemo.module.security.JwtAuthEntryPoint;
import com.habin.springdemo.module.security.JwtAuthenticationFilter;
import com.habin.springdemo.module.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;

import static java.util.Arrays.asList;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


/**
 * @author : 김하빈(hbkim@bpnsolution.com)
 * @description : 보안 정책(Jwt 토큰, Session 쿠키, Cors 리퀘스트 제약) 클래스
 * @Date : 2020. 10. 7.
 * @Time : 오전 9:13:30
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthEntryPoint authEntryPoint;
	private final JwtTokenProvider jwtTokenProvider;
	private final Environment env;

	private static final Profiles profile = Profiles.of("master", "develop");

	private static final String[] NOT_NEED_TOKEN_URLS = {"/swagger-ui/**", "/swagger-ui/index.html", "/v3/api-docs/**",
			"/rest-api-doc.html", "/signUp", "/login", "/refresh", "/static/**", "/css/**", "/js/**", "/img/**",
			"/lib/**", "/manifest.json", "/lodash.json"};

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(List.of("*"));
		configuration.setAllowedMethods(asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(asList("Cookie", "Accept", "Accept-Encoding", "TOKEN", "Authorization", "Cache-Control", "Content-Type"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		// static 디렉터리의 하위 파일 목록은 인증 무시 ( = 항상통과 )
		return (web) -> web.ignoring().antMatchers(NOT_NEED_TOKEN_URLS);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding("UTF-8");
		filter.setForceEncoding(true);
		http.addFilterBefore(filter, CsrfFilter.class);

		http.cors().configurationSource(corsConfigurationSource()).and()
				.csrf().disable() // rest api이므로 csrf 보안이 필요없으므로 disable처리.
				.httpBasic().disable() // rest api 이므로 기본설정 사용안함. 기본설정은 비인증시 로그인폼 화면으로 리다이렉트 된다.
				.formLogin().disable(); // form 기반의 로그인에 대해 비활성화 한다.

		if (env.acceptsProfiles(profile)) {
			http
					// 토큰을 활용하면 세션이 필요 없으므로 STATELESS로 설정하여 Session을 사용하지 않는다.
					.sessionManagement().sessionCreationPolicy(STATELESS)
					.and()
					.authorizeHttpRequests()
					.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
					.antMatchers(NOT_NEED_TOKEN_URLS).permitAll() // 토큰 없어도 됨
					.anyRequest().authenticated() // 토큰 있어야 함 / 관리자, 유저 둘다
					.and()
					.exceptionHandling().authenticationEntryPoint(authEntryPoint)
					.and()
					.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
							UsernamePasswordAuthenticationFilter.class);
		} else {
			http
					// 토큰을 활용하면 세션이 필요 없으므로 STATELESS로 설정하여 Session을 사용하지 않는다.
					.sessionManagement().sessionCreationPolicy(STATELESS)
					.and()
					.authorizeRequests() // 권한 체크
					.anyRequest().permitAll();
		}

		return http.build();
	}

}