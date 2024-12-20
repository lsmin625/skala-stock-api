package com.sk.skala.stockapi.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.CodeGroup;
import com.sk.skala.stockapi.service.CodeGroupService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/code-group")
public class CodeGroupController {

	private final CodeGroupService codeGroupService;

	@GetMapping("/list")
	public Response getList(@RequestParam(defaultValue = "") String keyword,
			@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int count)
			throws Exception {
		return codeGroupService.getCodeGroups(keyword, offset, count);
	}

	@GetMapping
	public Response get(@RequestParam String codeGroupId) throws Exception {
		return codeGroupService.getCodeGroupById(codeGroupId);
	}

	@PostMapping
	public Response update(@RequestBody CodeGroup codGroup) throws Exception {
		return codeGroupService.saveCodeGroup(codGroup);
	}

	@DeleteMapping
	public Response delete(@RequestBody CodeGroup codGroup) throws Exception {
		return codeGroupService.deleteCodeGroup(codGroup.getCodeGroupId());
	}
}
