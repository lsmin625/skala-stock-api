package com.sk.skala.stockapi.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.request.AuthRequest;
import com.sk.skala.stockapi.data.request.MaskingRequest;
import com.sk.skala.stockapi.service.AuthService;
import com.sk.skala.stockapi.tools.SecureTool;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	@GetMapping("/key")
	public Response key(@RequestParam(defaultValue = "16") int size) throws Exception {
		Map<String, Object> body = new HashMap<String, Object>();
		if (size == 16 || size == 24 || size == 32) {
			body.put("keyLength", size);
			body.put("key", SecureTool.generateKey(size));
		} else {
			body.put("keyLength", 16);
			body.put("key", SecureTool.generateKey(16));
		}
		Response response = new Response();
		response.setBody(body);
		return response;
	}

	@PostMapping("/code/send")
	public Response sendCode(@RequestBody AuthRequest request) throws Exception {
		return authService.sendCode(request);
	}

	@PostMapping("/masking/unlock")
	public Response unlockMasking(@RequestBody MaskingRequest request) throws Exception {
		return authService.unlockMasking(request);
	}

}
