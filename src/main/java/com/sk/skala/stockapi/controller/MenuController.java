package com.sk.skala.stockapi.controller;

import java.util.List;
import java.util.Map;

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
import com.sk.skala.stockapi.data.table.Menu;
import com.sk.skala.stockapi.service.MenuService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

	private final MenuService menuService;

	@GetMapping
	public Response get(@RequestParam Map<String, String> param) throws Exception {
		return menuService.find(param.get("systemId"), param.get("menuId"));
	}

	@GetMapping("/list")
	public Response list(@RequestParam(defaultValue = "0") Integer offset,
			@RequestParam(defaultValue = "10") Integer count, @RequestParam(defaultValue = "") String systemId,
			@RequestParam(defaultValue = "") String menuName) throws Exception {
		return menuService.findAllBySystemId(systemId, menuName, offset, count);
	}

	@GetMapping("/system")
	public Response system() throws Exception {
		return menuService.findSystems();
	}

	@GetMapping("/system/menu")
	public Response systemMenus(@RequestParam String systemId) throws Exception {
		return menuService.findSystemMenus(systemId);
	}

	@PostMapping
	public Response post(@RequestBody Menu menu) throws Exception {
		return menuService.save(menu);
	}

	@PostMapping("/bulk")
	public Response bulk(@RequestBody List<Menu> menus) throws Exception {
		return menuService.saveAll(menus);
	}

	@DeleteMapping
	public Response delete(@RequestBody Menu menu) throws Exception {
		return menuService.delete(menu);
	}

	@GetMapping("/excel/download")
	public ResponseEntity<ByteArrayResource> excelDownload(@RequestParam String systemId) throws Exception {
		return menuService.buildExcel(systemId);
	}

	@GetMapping("/excel/template")
	public ResponseEntity<ByteArrayResource> excelTemplate() throws Exception {
		return menuService.buildTemplate();
	}

	@PostMapping("/excel/upload")
	public Response excelUpload(@RequestParam("file") MultipartFile file) throws Exception {
		return menuService.parseExcel(file);
	}

	@GetMapping("/csv/download")
	public ResponseEntity<ByteArrayResource> csvDownload() throws Exception {
		return menuService.buildCsvFile();
	}

}
