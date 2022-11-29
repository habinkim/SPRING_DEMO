package com.habin.springdemo.module.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class LogAspect {

	private final Environment env;
	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
			.configure(FAIL_ON_NULL_FOR_PRIMITIVES, false)
			.configure(FAIL_ON_EMPTY_BEANS, false)
			.registerModule(new JavaTimeModule());

	@Pointcut("within(com.bpnsolution.demo.repository..*)")
	public void jpaQuery() {
	}

	@Pointcut("within(com.bpnsolution.demo.module..*)")
	public void module() {
	}

	@Pointcut("within(com.bpnsolution.demo.module.security..*)")
	public void securityModule() {
	}

	@Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
	public void getApi() {
	}

	@Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
	public void postApi() {
	}

	@Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
	public void putApi() {
	}

	@Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
	public void deleteApi() {
	}

	@Pointcut("getApi() || postApi() || putApi() || deleteApi()")
	public void restApi() {
	}

	@Around("@annotation(LogExecutionTime) || restApi() || jpaQuery() || module() && !securityModule()")
	public Object LogExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch("LogExecutionTime Aop");

		stopWatch.start();
		Object proceed = joinPoint.proceed();
		stopWatch.stop();

		String msg = new StringBuilder()
				.append(joinPoint.getSignature().getDeclaringType()).append(".")
				.append(joinPoint.getSignature().getName()).append(" : ")
				.append("running time = ")
				.append(stopWatch.getTotalTimeMillis()).append("ms")
				.toString();
		log.info(msg);

		return proceed;
	}

	@Around("restApi()")
	public Object LogRequestUrl(ProceedingJoinPoint joinPoint) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes) currentRequestAttributes()).getRequest();

		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		Method method = methodSignature.getMethod();

		String httpMethod = Stream.of(GetMapping.class, PutMapping.class, PostMapping.class,
						PatchMapping.class, DeleteMapping.class, RequestMapping.class)
				.filter(method::isAnnotationPresent)
				.map(LogAspect::getHttpMethod)
				.findFirst().orElse(null);

		String apiInfo = new StringBuilder()
				.append("called api is -- ").append(httpMethod).append(" ").append(request.getRequestURI())
				.toString();


		log.info(apiInfo);
		if (env.acceptsProfiles(Profiles.of("develop")) &&
				(request.getContentType() != null && !request.getContentType().contains("multipart"))) {
			String requestParam = new StringBuilder()
					.append("request parameters are -- ").append(System.lineSeparator())
					.append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(params(joinPoint)))
					.toString();
			log.info(requestParam);
		}
		return joinPoint.proceed(joinPoint.getArgs());
	}

	private static String getHttpMethod(Class<? extends Annotation> mappingClass) {
		return (mappingClass.getSimpleName().replace("Mapping", "")).toUpperCase();
	}

	private Map<String, Object> params(JoinPoint joinPoint) {
		CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
		String[] parameterNames = codeSignature.getParameterNames();
		Object[] args = joinPoint.getArgs();
		Map<String, Object> params = new HashMap<>();
		IntStream.range(0, parameterNames.length).forEach(i -> {
			if (!Objects.equals(parameterNames[i], "file")) {
				params.put(parameterNames[i], args[i]);
			}
		});
		return params;
	}

}
