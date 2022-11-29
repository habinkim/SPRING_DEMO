package com.habin.springdemo.module.singleton;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.time.format.DateTimeFormatter.ofPattern;

public class StaticLambdaExpression {

	private static final DateTimeFormatter DATE_TIME_FORMAT = ofPattern("yyyy/MM/dd HH:mm:ss", Locale.KOREA);
	private static final DateTimeFormatter DATE_TIME_FORMAT_2 = ofPattern("EEE MMM d yyyy HH:mm:ss zzz", Locale.ENGLISH);

	public static final BiFunction<Map<String, Object>, String, String> StringParam =
			(m, s) -> m.get(s).toString();

	public static final BiFunction<Map<String, Object>, String, List<String>> StringListParam =
			(m, s) -> {
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					return objectMapper.readValue(objectMapper.writeValueAsString(m.get(s)), new TypeReference<List<String>>() {
					});
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return null;
			};

	public static final BiFunction<Map<String, Object>, String, Long> LongParam =
			(m, s) -> m.get(s).toString().chars().allMatch(Character::isDigit) ?
					parseLong(m.get(s).toString()) : null;

	public static final BiFunction<Map<String, Object>, String, Integer> IntegerParam =
			(m, s) -> m.get(s).toString().chars().allMatch(Character::isDigit) ?
					parseInt(m.get(s).toString()) : null;

	public static final BiFunction<Map<String, Object>, String, LocalDate> LocalDateParam =
			(m, s) -> LocalDate.parse(m.get(s).toString());

	public static final BiFunction<Map<String, Object>, String, LocalDateTime> LocalDateTimeParam =
			(m, s) -> LocalDateTime.parse(m.get(s).toString(), DATE_TIME_FORMAT);

	public static final BiFunction<Map<String, Object>, String, LocalDateTime> LocalDateTimeParam2 =
			(m, s) -> {
				String s1 = m.get(s).toString();
				String s2 = new StringBuilder(s1).insert(s1.length() - 2, ":").toString();
				return ZonedDateTime.parse(s2, DATE_TIME_FORMAT_2).toLocalDateTime();
			};


	public static final BiFunction<Map<String, Object>, String, String> DynamicStringParam =
			(m, s) -> m.containsKey(s) ? m.get(s).toString() : null;

	public static final BiFunction<Map<String, Object>, String, Long> DynamicLongParam =
			(m, s) -> m.containsKey(s) && m.get(s).toString().chars().allMatch(Character::isDigit) ?
					parseLong(m.get(s).toString()) : null;

	public static final BiFunction<Map<String, Object>, String, Integer> DynamicIntegerParam =
			(m, s) -> m.containsKey(s) && m.get(s).toString().chars().allMatch(Character::isDigit) ?
					parseInt(m.get(s).toString()) : null;

	public static final BiFunction<Map<String, Object>, String, LocalDate> DynamicLocalDateParam =
			(m, s) -> m.containsKey(s) ? LocalDate.parse(m.get(s).toString()) : null;

	public static final BiFunction<Map<String, Object>, String, LocalDateTime> DynamicLocalDateTimeParam =
			(m, s) -> m.containsKey(s) ? LocalDateTime.parse(m.get(s).toString(), DATE_TIME_FORMAT) : null;


	public static final BiFunction<Map<String, Object>, String, LocalDateTime> DynamicLocalDateTimeParam2 =
			(m, s) -> {
				if (m.containsKey(s)) {
					String s1 = m.get(s).toString();
					String s2 = new StringBuilder(m.get(s).toString()).insert(s1.length() - 2, ":").toString();
					return ZonedDateTime.parse(s2, DATE_TIME_FORMAT_2).toLocalDateTime();

				} else {
					return null;
				}
			};

	public static final Function<String, Supplier<NoSuchElementException>> getNSEE =
			(s) -> () -> new NoSuchElementException(s + "이(가) 존재하지 않습니다.");

	public static final BiFunction<Map<String, Object>, String, List<Long>> LongListParam =
			(m, s) -> {
				Pattern p = Pattern.compile("\\d+");

				String idx = m.get(s).toString();
				Matcher matcher = p.matcher(idx);
				List<Long> idxExt = new ArrayList<>();

				while (matcher.find()) {
					idxExt.add(parseLong(matcher.group()));
				}

				return idxExt;
			};

	public static final BiFunction<Map<String, Object>, String, List<Long>> DynamicLongListParam =
			(m, s) -> {
				if (m.containsKey(s)) {
					Pattern p = Pattern.compile("\\d+");

					String idx = m.get(s).toString();
					Matcher matcher = p.matcher(idx);
					List<Long> idxExt = new ArrayList<>();

					while (matcher.find()) {
						idxExt.add(parseLong(matcher.group()));
					}

					return idxExt;

				} else {
					return null;
				}
			};

	public static final BiFunction<Map<String, Object>, String, List<Integer>> IntegerListParam =
			(m, s) -> {
				Pattern p = Pattern.compile("\\d+");

				String idx = m.get(s).toString();
				Matcher matcher = p.matcher(idx);
				List<Integer> idxExt = new ArrayList<>();

				while (matcher.find()) {
					idxExt.add(parseInt(matcher.group()));
				}

				return idxExt;
			};

	private StaticLambdaExpression() {}

}
