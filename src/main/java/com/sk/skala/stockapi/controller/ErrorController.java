package com.sk.skala.stockapi.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.service.ErrorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/error")
@RequiredArgsConstructor
public class ErrorController {

	private final ErrorService errorService;

	@GetMapping(value = "/system", produces = "application/json")
	public Response system(@RequestParam Map<String, String> param) throws Exception {
		return errorService.getException();
	}

	@GetMapping(value = "/parameter", produces = "application/json")
	public Response parameter(@RequestParam Map<String, String> param) throws Exception {
		return errorService.getParameterException();
	}

	@GetMapping(value = "/response", produces = "application/json")
	public Response response(@RequestParam Map<String, String> param) throws Exception {
		return errorService.getResponseException();
	}

}
