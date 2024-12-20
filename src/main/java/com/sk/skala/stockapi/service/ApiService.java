package com.sk.skala.stockapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sk.skala.stockapi.config.Constant;
import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.ExcelData;
import com.sk.skala.stockapi.data.common.ExcelData.Header;
import com.sk.skala.stockapi.data.common.PagedList;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.ApiMst;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.repository.ApiRepository;
import com.sk.skala.stockapi.tools.CsvFileTool;
import com.sk.skala.stockapi.tools.ExcelTool;
import com.sk.skala.stockapi.tools.StringTool;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiService {
	private static final String EXCEL_SHEET_NAME = "apis";

	private final ApiRepository apiRepository;

	public Response findAll(String keyword, int offset, int count) throws Exception {
		Pageable pageable = PageRequest.of(offset, count, Sort.by(Sort.Direction.ASC, "apiPath"));
		String wildcard = StringTool.like(keyword);
		Page<ApiMst> paged = apiRepository.findAllByApiPathLikeOrApiNameLike(wildcard, wildcard, pageable);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setOffset(pageable.getPageNumber());
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response save(ApiMst api) throws Exception {
		if (StringTool.isAnyEmpty(api.getApiPath(), api.getApiName())) {
			throw new ParameterException("apiPath", "apiName");
		}

		Optional<ApiMst> option = apiRepository.findByApiPath(api.getApiPath());
		if (option.isPresent()) {
			ApiMst apiMst = option.get();
			api.setId(apiMst.getId());
		} else {
			api.setId(0L);
		}
		apiRepository.save(api);
		return new Response();
	}

	public Response saveAll(List<ApiMst> apis) throws Exception {
		for (ApiMst api : apis) {
			Optional<ApiMst> option = apiRepository.findByApiPath(api.getApiPath());
			if (option.isPresent()) {
				ApiMst apiMst = option.get();
				api.setId(apiMst.getId());
			} else {
				api.setId(0L);
			}
			apiRepository.save(api);
		}
		return new Response();
	}

	public Response delete(ApiMst api) throws Exception {
		if (apiRepository.existsById(api.getId())) {
			apiRepository.deleteById(api.getId());
		} else {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		return new Response();
	}

	private List<Header> getExcelHeaders() {
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("API경로", "apiPath"));
		headers.add(new Header("API명", "apiName"));
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
		List<ApiMst> apis = ExcelTool.parse(file, headers, ApiMst.class);
		return saveAll(apis);
	}

	public ResponseEntity<ByteArrayResource> buildExcel(String keyword) throws Exception {
		ExcelData excelData = new ExcelData();
		excelData.setSheetname(EXCEL_SHEET_NAME);
		excelData.setHeaders(getExcelHeaders());

		Pageable pageable = PageRequest.of(0, Constant.EXCEL_MAX_ROWS);
		String wildcard = StringTool.like(keyword);
		Page<ApiMst> paged = apiRepository.findAllByApiPathLikeOrApiNameLike(wildcard, wildcard, pageable);
		excelData.setRows(paged.getContent());

		return ExcelTool.build(excelData);
	}

	public ResponseEntity<ByteArrayResource> buildCsvFile(String keyword) throws Exception {
		String filename = EXCEL_SHEET_NAME + "-" + System.currentTimeMillis() + ".csv";
		List<Header> headers = getExcelHeaders();

		return CsvFileTool.buildCsv(filename, headers, writer -> {
			int page = 0;
			Page<ApiMst> paged;

			do {
				Pageable pageable = PageRequest.of(page++, Constant.EXCEL_PAGE_ROWS);
				String wildcard = StringTool.like(keyword);
				paged = apiRepository.findAllByApiPathLikeOrApiNameLike(wildcard, wildcard, pageable);
				List<List<Object>> rows = ExcelData.buildRows(headers, paged.getContent());
				CsvFileTool.writeRows(writer, headers, rows);
			} while (paged.hasNext());
		});
	}
}
