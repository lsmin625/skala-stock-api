package com.sk.skala.stockapi.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.stockapi.data.common.AccountInfo;
import com.sk.skala.stockapi.data.common.AccountSession;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.service.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

	private final AccountService accountService;

	@GetMapping("/available")
	public Response available(@RequestParam String userId) throws Exception {
		return accountService.available(userId);
	}

	@PostMapping("/signup")
	public Response signup(@RequestBody AccountInfo info) throws Exception {
		return accountService.signup(info);
	}

	@PostMapping("/login")
	public Response login(@RequestBody AccountInfo info) throws Exception {
		return accountService.login(info);
	}

	@PostMapping("/token")
	public Response token(@RequestBody AccountInfo info) throws Exception {
		return accountService.token(info);
	}

	@GetMapping("/sso")
	public Response sso(@RequestParam String userId, @RequestParam String sessionId) throws Exception {
		return accountService.sso(userId, sessionId);
	}

	@PostMapping("/logout")
	public Response logout(@RequestBody AccountSession accountSession) throws Exception {
		return accountService.logout(accountSession);
	}

	@PutMapping("/user-group")
	public Response userGroup(@RequestBody AccountInfo info) throws Exception {
		return accountService.updateUserGroup(info);
	}

	@PutMapping("/password")
	public Response password(@RequestBody Map<String, String> param) throws Exception {
		return accountService.updatePassword(param);
	}

	@PutMapping("/profile")
	public Response profile(@RequestBody AccountInfo info) throws Exception {
		return accountService.updateProfile(info);
	}
}
