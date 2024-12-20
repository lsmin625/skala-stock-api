package com.sk.skala.stockapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.stockapi.aop.SkipLogging;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.service.ProxyLogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
public class ProxyLogController {

	private final ProxyLogService proxyLogService;

	@SkipLogging
	@GetMapping("/path")
	public Response findByApiUrlLike(@RequestParam(defaultValue = "") String keyword,
			@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int count)
			throws Exception {
		return proxyLogService.findByApiUrlLike(keyword, offset, count);
	}

	@SkipLogging
	@GetMapping("/request")
	public Response findByRequestParamsLikeOrRequestBodyLike(@RequestParam(defaultValue = "") String keyword,
			@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int count)
			throws Exception {
		return proxyLogService.findByRequestParamsLikeOrRequestBodyLike(keyword, offset, count);
	}

	@SkipLogging
	@GetMapping("/result")
	public Response findByApiResultAndApiUrlLike(@RequestParam String result,
			@RequestParam(defaultValue = "") String keyword, @RequestParam(defaultValue = "0") int offset,
			@RequestParam(defaultValue = "10") int count) throws Exception {
		return proxyLogService.findByApiResultAndApiUrlLike(result, keyword, offset, count);
	}

	@SkipLogging
	@GetMapping("/response")
	public Response findByResponseBodyLike(@RequestParam(defaultValue = "") String keyword,
			@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int count)
			throws Exception {
		return proxyLogService.findByResponseBodyLike(keyword, offset, count);
	}

	@SkipLogging
	@GetMapping("/time")
	public Response findByElapsedTimeGreaterThan(@RequestParam long seconds,
			@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int count)
			throws Exception {
		return proxyLogService.findByElapsedTimeGreaterThan(seconds, offset, count);
	}
}
