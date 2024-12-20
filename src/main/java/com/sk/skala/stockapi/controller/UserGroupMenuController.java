package com.sk.skala.stockapi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.UserGroupMenu;
import com.sk.skala.stockapi.service.UserGroupMenuService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-group-menus")
@RequiredArgsConstructor
public class UserGroupMenuController {

	private final UserGroupMenuService userGroupMenuService;

	@GetMapping("/list")
	public Response list(@RequestParam String userGroupId, @RequestParam(defaultValue = "0") Integer offset,
			@RequestParam(defaultValue = "10") Integer count) throws Exception {
		return userGroupMenuService.findAll(userGroupId, offset, count);
	}

	@GetMapping("/all")
	public Response listAll(@RequestParam String userGroupId, @RequestParam String systemId) throws Exception {
		return userGroupMenuService.findAll(userGroupId, systemId);
	}

	@GetMapping
	public Response get(@RequestBody UserGroupMenu req) throws Exception {
		return userGroupMenuService.find(req);
	}

	@PostMapping()
	public Response post(@RequestBody UserGroupMenu req) throws Exception {
		return userGroupMenuService.save(req);
	}

	@PostMapping("/bulk")
	public Response bulk(@RequestBody List<UserGroupMenu> list) throws Exception {
		return userGroupMenuService.saveAll(list);
	}

	@DeleteMapping()
	public Response delete(@RequestBody UserGroupMenu req) throws Exception {
		return userGroupMenuService.delete(req);
	}
}
