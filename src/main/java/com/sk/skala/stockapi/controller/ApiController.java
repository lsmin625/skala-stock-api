package com.sk.skala.stockapi.controller;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.ApiMst;
import com.sk.skala.stockapi.service.ApiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/apis")
public class ApiController {

	private final ApiService apiService;

	@GetMapping("/list")
	public Response list(@RequestParam(defaultValue = "0") Integer offset,
			@RequestParam(defaultValue = "10") Integer count, @RequestParam(defaultValue = "") String keyword)
			throws Exception {
		return apiService.findAll(keyword, offset, count);
	}

	@PostMapping
	public Response post(@RequestBody ApiMst api) throws Exception {
		return apiService.save(api);
	}

	@PostMapping("/bulk")
	public Response bulk(@RequestBody List<ApiMst> apis) throws Exception {
		return apiService.saveAll(apis);
	}

	@DeleteMapping
	public Response delete(@RequestBody ApiMst api) throws Exception {
		return apiService.delete(api);
	}

	@GetMapping("/excel/download")
	public ResponseEntity<ByteArrayResource> excelDownload(@RequestParam(defaultValue = "") String keyword)
			throws Exception {
		return apiService.buildExcel(keyword);
	}

	@GetMapping("/excel/template")
	public ResponseEntity<ByteArrayResource> excelTemplate() throws Exception {
		return apiService.buildTemplate();
	}

	@PostMapping("/excel/upload")
	public Response excelUpload(@RequestParam("file") MultipartFile file) throws Exception {
		return apiService.parseExcel(file);
	}

	@GetMapping("/csv/download")
	public ResponseEntity<ByteArrayResource> csvDownload(@RequestParam(defaultValue = "") String keyword)
			throws Exception {
		return apiService.buildCsvFile(keyword);
	}

}
