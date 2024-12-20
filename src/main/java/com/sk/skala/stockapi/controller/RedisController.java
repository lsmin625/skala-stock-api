package com.sk.skala.stockapi.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.service.RedisService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
public class RedisController {

	private final RedisService redisService;

	@GetMapping(value = "/keys", produces = "application/json")
	public Response keys(@RequestParam String prefix) throws Exception {
		return redisService.getKeys(prefix);
	}

	@GetMapping(value = "/list", produces = "application/json")
	public Response list(@RequestParam String prefix) throws Exception {
		return redisService.getKeysWithValue(prefix);
	}

	@GetMapping(value = "/value", produces = "application/json")
	public Response getValue(@RequestParam String key) throws Exception {
		return redisService.getKeyValue(key);
	}

	@PostMapping(value = "/value", produces = "application/json")
	public Response postValue(@RequestBody Map<String, Object> param) throws Exception {
		return redisService.setKeyValue(param);
	}

	@DeleteMapping(value = "/delete", produces = "application/json")
	public Response delete(@RequestBody Map<String, String> map) throws Exception {
		return redisService.deleteKey(map.get("key"));
	}
}
