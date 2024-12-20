package com.sk.skala.stockapi.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.UserGroupApi;
import com.sk.skala.stockapi.service.UserGroupApiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-group-apis")
public class UserGroupApiController {

	private final UserGroupApiService userGroupApiService;

	@GetMapping()
	public Response list(@RequestParam String userGroupId) throws Exception {
		return userGroupApiService.findAll(userGroupId);
	}

	@PostMapping()
	public Response post(@RequestBody UserGroupApi req) throws Exception {
		return userGroupApiService.save(req);
	}

	@DeleteMapping()
	public Response delete(@RequestBody UserGroupApi req) throws Exception {
		return userGroupApiService.delete(req);
	}
}
