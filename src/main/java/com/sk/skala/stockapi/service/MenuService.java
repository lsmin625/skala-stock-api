package com.sk.skala.stockapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sk.skala.stockapi.config.Constant;
import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.ExcelData;
import com.sk.skala.stockapi.data.common.ExcelData.Header;
import com.sk.skala.stockapi.data.common.PagedList;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.Menu;
import com.sk.skala.stockapi.data.table.MenuMstId;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.repository.MenuRepository;
import com.sk.skala.stockapi.tools.CsvFileTool;
import com.sk.skala.stockapi.tools.ExcelTool;
import com.sk.skala.stockapi.tools.StringTool;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {

	private final MenuRepository menuRepository;
	private final CodeGroupService codeGroupService;

	private static final String CDOE_GROUP_systemId = "system_id";
	private static final String EXCEL_SHEET_NAME = "menus";

	public Response find(String systemId, String menuId) throws Exception {
		if (StringTool.isAnyEmpty(systemId, menuId)) {
			throw new ParameterException("systemId", "menuId");
		}

		Optional<Menu> option = menuRepository.findById(new MenuMstId(systemId, menuId));
		if (option.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		Response response = new Response();
		response.setBody(option.get());
		return response;
	}

	public Response findSystems() throws Exception {
		List<Menu> menuList = menuRepository.findAllGroupedBySystemId();

		PagedList pagedList = new PagedList();
		pagedList.setTotal(menuList.size());
		pagedList.setOffset(0);
		pagedList.setCount(menuList.size());
		pagedList.setList(menuList);

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response findSystemMenus(String systemId) throws Exception {
		List<Menu> menuList = menuRepository.findAllBySystemId(systemId);
		if (menuList.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}

		PagedList pagedList = new PagedList();
		pagedList.setTotal(menuList.size());
		pagedList.setOffset(0);
		pagedList.setCount(menuList.size());
		pagedList.setList(menuList);

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response findAll(String menuName, int offset, int count) throws Exception {
		Pageable pageable = PageRequest.of(offset, count);
		Page<Menu> paged = menuRepository.findByMenuNameLike(StringTool.like(menuName), pageable);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setOffset(pageable.getPageNumber());
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response findAllBySystemId(String systemId, String menuName, int offset, int count) throws Exception {
		Pageable pageable = PageRequest.of(offset, count);
		Page<Menu> paged = null;
		if (StringTool.isEmpty(systemId)) {
			paged = menuRepository.findByMenuNameLike(StringTool.like(menuName), pageable);
		} else {
			paged = menuRepository.findBySystemIdAndMenuNameLike(systemId, StringTool.like(menuName), pageable);
		}

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setOffset(pageable.getPageNumber());
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response save(Menu menu) throws Exception {
		if (StringTool.isAnyEmpty(menu.getSystemId(), menu.getMenuId())) {
			throw new ParameterException("systemId", "menuId");
		}

		if (!codeGroupService.isCodeInGroup(CDOE_GROUP_systemId, menu.getSystemId())) {
			throw new ResponseException(Error.CODE_MISSED_IN_CODE_GROUP, CDOE_GROUP_systemId);
		}

		menuRepository.save(menu);
		return new Response();
	}

	public Response saveAll(List<Menu> menus) throws Exception {
		for (Menu menu : menus) {
			if (StringTool.isAnyEmpty(menu.getSystemId(), menu.getMenuId())) {
				throw new ParameterException("systemId", "menuId");
			}

			if (!codeGroupService.isCodeInGroup(CDOE_GROUP_systemId, menu.getSystemId())) {
				throw new ResponseException(Error.CODE_MISSED_IN_CODE_GROUP, CDOE_GROUP_systemId);
			}
		}
		menuRepository.saveAll(menus);
		return new Response();
	}

	public Response delete(Menu menu) throws Exception {
		MenuMstId menuMstId = new MenuMstId(menu.getSystemId(), menu.getMenuId());
		if (menuRepository.existsById(menuMstId)) {
			menuRepository.deleteById(menuMstId);
		} else {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		return new Response();
	}

	private List<Header> getExcelHeaders() {
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("시스템ID", "systemId"));
		headers.add(new Header("시스템명", "systemName"));
		headers.add(new Header("메뉴ID", "menuId"));
		headers.add(new Header("메뉴명", "menuName"));
		return headers;
	}

	public ResponseEntity<ByteArrayResource> buildTemplate() throws Exception {
		ExcelData excelData = new ExcelData();
		excelData.setSheetname(EXCEL_SHEET_NAME);
		excelData.setHeaders(getExcelHeaders());

		List<List<Object>> rows = new ArrayList<List<Object>>();
		excelData.setRows(rows);
		return ExcelTool.build(excelData);
	}

	public Response parseExcel(MultipartFile file) throws Exception {
		List<Header> headers = getExcelHeaders();
		List<Menu> menus = ExcelTool.parse(file, headers, Menu.class);
		return saveAll(menus);
	}

	public ResponseEntity<ByteArrayResource> buildExcel(String systemId) throws Exception {
		ExcelData excelData = new ExcelData();
		excelData.setSheetname(EXCEL_SHEET_NAME);
		excelData.setHeaders(getExcelHeaders());

		List<Menu> menuList = menuRepository.findAllBySystemId(systemId);
		excelData.setRows(menuList);

		return ExcelTool.build(excelData);
	}

	public ResponseEntity<ByteArrayResource> buildCsvFile() throws Exception {
		String filename = EXCEL_SHEET_NAME + "-" + System.currentTimeMillis() + ".csv";
		List<Header> headers = getExcelHeaders();

		return CsvFileTool.buildCsv(filename, headers, writer -> {
			int page = 0;
			Page<Menu> paged;

			do {
				Pageable pageable = PageRequest.of(page++, Constant.EXCEL_PAGE_ROWS);
				paged = menuRepository.findByMenuNameLike(StringTool.like(""), pageable);

				List<List<Object>> rows = ExcelData.buildRows(headers, paged.getContent());
				CsvFileTool.writeRows(writer, headers, rows);
			} while (paged.hasNext());
		});
	}

}
