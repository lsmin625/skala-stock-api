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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sk.skala.stockapi.config.Constant;
import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.ExcelData;
import com.sk.skala.stockapi.data.common.ExcelData.Header;
import com.sk.skala.stockapi.data.common.PagedList;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.User;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.repository.UserRepository;
import com.sk.skala.stockapi.tools.CsvFileTool;
import com.sk.skala.stockapi.tools.ExcelTool;
import com.sk.skala.stockapi.tools.StringTool;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private static final String EXCEL_SHEET_NAME = "users";

	private final UserRepository userRepository;

	public Response find(String userId) throws Exception {
		if (StringTool.isEmpty(userId)) {
			throw new ParameterException("userId");
		}

		Optional<User> option = userRepository.findById(userId);
		if (option.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		Response response = new Response();
		User user = option.get();
		response.setBody(user);
		return response;
	}

	public Response findAll(String userName, int offset, int count) throws Exception {
		Pageable pageable = PageRequest.of(offset, count, Sort.by(Sort.Direction.ASC, "userId"));
		Page<User> paged = userRepository.findByUserNameLike(StringTool.like(userName), pageable);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setOffset(offset);
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response save(User user) throws Exception {
		if (userRepository.existsById(user.getUserId())) {
			throw new ResponseException(Error.DATA_DUPLICATED, user.getUserId());
		}
		userRepository.save(user);
		return new Response();
	}

	public Response saveAll(List<User> users) throws Exception {
		for (User user : users) {
			if (userRepository.existsById(user.getUserId())) {
				throw new ResponseException(Error.DATA_DUPLICATED, user.getUserId());
			}
		}
		userRepository.saveAll(users);
		return new Response();
	}

	@Transactional
	public Response saveAllTransactional(List<User> users) throws Exception {
		for (User user : users) {
			if (userRepository.existsById(user.getUserId())) {
				throw new ResponseException(Error.DATA_DUPLICATED, user.getUserId());
			}
			userRepository.save(user);
		}
		return new Response();
	}

	public Response update(User user) throws Exception {
		Optional<User> existingUser = userRepository.findById(user.getUserId());
		if (existingUser.isPresent()) {
			userRepository.save(user);
		} else {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		return new Response();
	}

	public Response delete(User user) throws Exception {
		if (userRepository.existsById(user.getUserId())) {
			userRepository.deleteById(user.getUserId());
		} else {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		return new Response();
	}

	private List<Header> getExcelHeaders(boolean isTemplate) {
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("사용자ID", "userId"));
		headers.add(new Header("사용자그룹ID", "userGroupId"));
		headers.add(new Header("사용자명", "userName"));
		if (isTemplate) {
			headers.add(new Header("비밀번호", "userPassword"));
		}
		headers.add(new Header("Email", "userEmail"));
		headers.add(new Header("Phone", "userPhone"));
		return headers;
	}

	public ResponseEntity<ByteArrayResource> buildTemplate() throws Exception {
		ExcelData excelData = new ExcelData();
		excelData.setSheetname(EXCEL_SHEET_NAME);
		excelData.setHeaders(getExcelHeaders(true));

		List<List<Object>> rows = new ArrayList<List<Object>>();
		excelData.setRows(rows);
		return ExcelTool.build(excelData);
	}

	public Response parseExcel(MultipartFile file) throws Exception {
		List<Header> headers = getExcelHeaders(true);
		List<User> users = ExcelTool.parse(file, headers, User.class);
		return saveAll(users);
	}

	public ResponseEntity<ByteArrayResource> buildExcel(String userName) throws Exception {
		ExcelData excelData = new ExcelData();
		excelData.setSheetname(EXCEL_SHEET_NAME);
		excelData.setHeaders(getExcelHeaders(false));

		Pageable pageable = PageRequest.of(0, Constant.EXCEL_MAX_ROWS);
		Page<User> paged = userRepository.findByUserNameLike(StringTool.like(userName), pageable);
		excelData.setRows(paged.getContent());

		return ExcelTool.build(excelData);
	}

	public ResponseEntity<ByteArrayResource> buildCsvFile(String userName) throws Exception {
		String filename = EXCEL_SHEET_NAME + "-" + System.currentTimeMillis() + ".csv";
		List<Header> headers = getExcelHeaders(false);

		return CsvFileTool.buildCsv(filename, headers, writer -> {
			int page = 0;
			Page<User> paged;

			do {
				Pageable pageable = PageRequest.of(page++, Constant.EXCEL_PAGE_ROWS);
				paged = userRepository.findByUserNameLike(StringTool.like(userName), pageable);
				List<List<Object>> rows = ExcelData.buildRows(headers, paged.getContent());
				CsvFileTool.writeRows(writer, headers, rows);
			} while (paged.hasNext());
		});
	}

}
