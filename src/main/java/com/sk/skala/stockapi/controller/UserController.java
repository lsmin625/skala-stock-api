package com.sk.skala.stockapi.controller;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.User;
import com.sk.skala.stockapi.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping
	public Response get(@RequestParam Map<String, String> param) throws Exception {
		return userService.find(param.get("userId"));
	}

	@GetMapping("/list")
	public Response list(@RequestParam(defaultValue = "0") Integer offset,
			@RequestParam(defaultValue = "10") Integer count, @RequestParam(defaultValue = "") String userName)
			throws Exception {
		return userService.findAll(userName, offset, count);
	}

	@PostMapping
	public Response post(@RequestBody User user) throws Exception {
		return userService.save(user);
	}

	@PostMapping("/bulk")
	public Response bulk(@RequestBody List<User> users) throws Exception {
		return userService.saveAllTransactional(users);
	}

	@PutMapping
	public Response put(@RequestBody User user) throws Exception {
		return userService.update(user);
	}

	@DeleteMapping
	public Response delete(@RequestBody User user) throws Exception {
		return userService.delete(user);
	}

	@GetMapping("/excel/download")
	public ResponseEntity<ByteArrayResource> excelDownload(@RequestParam(defaultValue = "") String userName)
			throws Exception {
		return userService.buildExcel(userName);
	}

	@GetMapping("/excel/template")
	public ResponseEntity<ByteArrayResource> excelTemplate() throws Exception {
		return userService.buildTemplate();
	}

	@PostMapping("/excel/upload")
	public Response excelUpload(@RequestParam("file") MultipartFile file) throws Exception {
		return userService.parseExcel(file);
	}

}
