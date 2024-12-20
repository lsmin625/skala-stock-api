package com.sk.skala.stockapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.sk.skala.stockapi.config.Constant;
import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.PagedList;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.Code;
import com.sk.skala.stockapi.data.table.CodeGroup;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.repository.CodeGroupRepository;
import com.sk.skala.stockapi.tools.JsonTool;
import com.sk.skala.stockapi.tools.StringTool;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodeGroupService {
	private final CodeGroupRepository codeGroupRepository;
	private final StringRedisTemplate stringRedisTemplate;

	public Response getCodeGroups(String keyword, int offset, int count) throws Exception {
		String wildcard = StringTool.like(keyword);
		Pageable pageable = PageRequest.of(offset, count);
		Page<CodeGroup> paged = codeGroupRepository.findByCodeGroupNameLike(wildcard, pageable);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setOffset(pageable.getPageNumber());
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response getCodeGroupById(String codeGroupId) throws Exception {
		if (StringTool.isEmpty(codeGroupId)) {
			throw new ParameterException("codeGroupId is empty");
		}

		CodeGroup codeGroup = readThroughCache(codeGroupId);
		if (codeGroup == null) {
			throw new ResponseException(Error.DATA_NOT_FOUND, "codeGroupId=" + codeGroupId);
		}
		List<Code> codeList = codeGroup.getCodeList();

		PagedList pagedList = new PagedList();
		pagedList.setTotal(codeList.size());
		pagedList.setOffset(0);
		pagedList.setCount(codeList.size());
		pagedList.setList(codeList);

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response saveCodeGroup(CodeGroup codeGroup) throws Exception {
		if (StringTool.isAnyEmpty(codeGroup.getCodeGroupId(), codeGroup.getCodeGroupName())) {
			throw new ParameterException("Code Group ID or Name is empty");
		}

		List<Code> list = codeGroup.getCodeList();
		if (list.size() < 1) {
			throw new ParameterException("Code should be at least two or more");
		}
		for (Code code : list) {
			if (StringTool.isAnyEmpty(code.getCodeName(), code.getCodeId())) {
				throw new ParameterException("Code Name or ID is empty");
			}
		}

		codeGroupRepository.save(codeGroup);
		stringRedisTemplate.delete(Constant.BFF_CODE_REDIS + codeGroup.getCodeGroupId());
		return new Response();
	}

	public Response deleteCodeGroup(String codeGroupId) throws Exception {
		if (!codeGroupRepository.existsById(codeGroupId)) {
			throw new ResponseException(Error.DATA_NOT_FOUND, "codeGroupId=" + codeGroupId);
		}

		codeGroupRepository.deleteById(codeGroupId);
		stringRedisTemplate.delete(Constant.BFF_CODE_REDIS + codeGroupId);
		return new Response();
	}

	private CodeGroup readThroughCache(String codeGroupId) {
		String value = stringRedisTemplate.opsForValue().get(Constant.BFF_CODE_REDIS + codeGroupId);
		if (value != null) {
			return JsonTool.toObject(value, CodeGroup.class);
		}

		Optional<CodeGroup> option = codeGroupRepository.findById(codeGroupId);
		if (!option.isEmpty()) {
			stringRedisTemplate.opsForValue().set(Constant.BFF_CODE_REDIS + codeGroupId,
					JsonTool.toString(option.get()));
			return option.get();
		}

		return null;
	}

	public boolean isCodeInGroup(String codeGroupId, String codeId) {
		CodeGroup codeGroup = readThroughCache(codeGroupId);
		if (codeGroup != null) {
			for (Code code : codeGroup.getCodeList()) {
				if (code.getCodeId().equals(codeId)) {
					return true;
				}
			}
		}
		return false;
	}
}
