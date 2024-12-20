package com.sk.skala.stockapi.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.PagedList;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.tools.JsonTool;
import com.sk.skala.stockapi.tools.StringTool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {

	private final StringRedisTemplate stringRedisTemplate;

	static final int PAGE = 100;

	public Response getKeyValue(String key) throws Exception {
		if (StringTool.isEmpty(key)) {
			throw new ParameterException("key");
		}

		Map<String, Object> body = getKeyValueTimeout(key);
		if (body == null) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		Response response = new Response();
		response.setBody(body);
		return response;
	}

	public Response getKeys(String prefix) throws Exception {
		if (StringTool.isAnyEmpty(prefix)) {
			throw new ParameterException("prefix");
		}

		List<String> keys = new ArrayList<>();
		ScanOptions options = ScanOptions.scanOptions().match(prefix + "*").count(PAGE).build();
		Cursor<String> scanCursor = stringRedisTemplate.scan(options);
		while (scanCursor.hasNext()) {
			keys.add(new String(scanCursor.next()));
			if (keys.size() >= PAGE) {
				break;
			}
		}

		Map<String, Object> body = new HashMap<>();
		body.put("prefix", prefix);
		body.put("keys", keys);

		Response response = new Response();
		response.setBody(body);
		return response;
	}

	public Response getKeysWithValue(String prefix) throws Exception {
		if (StringTool.isAnyEmpty(prefix)) {
			throw new ParameterException("prefix");
		}

		List<Map<String, Object>> valueList = new ArrayList<>();
		ScanOptions options = ScanOptions.scanOptions().match(prefix + "*").count(PAGE).build();
		Cursor<String> scanCursor = stringRedisTemplate.scan(options);
		while (scanCursor.hasNext()) {
			valueList.add(getKeyValueTimeout(new String(scanCursor.next())));
			if (valueList.size() >= PAGE) {
				break;
			}
		}
		PagedList pagedList = new PagedList();
		pagedList.setTotal(valueList.size());
		pagedList.setOffset(0);
		pagedList.setCount(valueList.size());
		pagedList.setList(valueList);

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Map<String, Object> getKeyValueTimeout(String key) throws Exception {
		Boolean hasKey = stringRedisTemplate.hasKey(key);
		if (Boolean.FALSE.equals(hasKey)) {
			return null;
		}
		String value = stringRedisTemplate.opsForValue().get(key);

		Map<String, Object> map = new HashMap<>();
		map.put("key", key);
		map.put("value", JsonTool.toMap(value));
		map.put("timeout", stringRedisTemplate.getExpire(key, TimeUnit.SECONDS));

		return map;
	}

	public Response setKeyValue(Map<String, Object> param) throws Exception {
		String key = (String) param.get("key");
		Object value = param.get("value");

		if (StringTool.isEmpty(key)) {
			throw new ParameterException("key");
		}

		Duration timeout = Duration.ofSeconds(0);
		if (param.get("timeout") != null) {
			timeout = Duration.ofSeconds((Integer) param.get("timeout"));
		}

		if (value instanceof String) {
			stringRedisTemplate.opsForValue().set(key, (String) value, timeout);
		} else {
			stringRedisTemplate.opsForValue().set(key, JsonTool.toString(value), timeout);
		}

		return new Response();
	}

	public Response deleteKey(String key) throws Exception {
		log.info("[{}]", key);
		if (StringTool.isEmpty(key)) {
			throw new ParameterException("key");
		}

		Boolean hasKey = stringRedisTemplate.hasKey(key);
		if (Boolean.FALSE.equals(hasKey)) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		stringRedisTemplate.delete(key);
		return new Response();
	}

}
