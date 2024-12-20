package com.sk.skala.stockapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.ApiMst;
import com.sk.skala.stockapi.data.table.UserGroupApi;
import com.sk.skala.stockapi.data.table.UserGroupApi.AuthApi;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.repository.ApiRepository;
import com.sk.skala.stockapi.repository.UserGroupApiRepository;
import com.sk.skala.stockapi.tools.StringTool;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserGroupApiService {

	private final UserGroupApiRepository userGroupApiRepository;
	private final ApiRepository apiRepository;
	private final CodeGroupService codeGroupService;

	private static final String CDOE_GROUP_userAuthGroup = "user_auth_group";

	public Response findAll(String userGroupId) throws RuntimeException {
		if (StringTool.isEmpty(userGroupId)) {
			throw new ParameterException("userGroupId");
		}

		Optional<UserGroupApi> option = userGroupApiRepository.findByUserGroupId(userGroupId);
		if (option.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND, userGroupId);
		}
		Response response = new Response();
		response.setBody(option.get());
		return response;
	}

	public Response save(UserGroupApi item) throws RuntimeException {
		if (StringTool.isAnyEmpty(item.getUserGroupId())) {
			throw new ParameterException("userGroupId");
		}

		if (!codeGroupService.isCodeInGroup(CDOE_GROUP_userAuthGroup, item.getUserGroupId())) {
			throw new ResponseException(Error.CODE_MISSED_IN_CODE_GROUP, CDOE_GROUP_userAuthGroup);
		}

		List<AuthApi> apiList = item.getAllowedApiList();
		for (AuthApi api : apiList) {
			Optional<ApiMst> option = apiRepository.findByApiPath(api.getApiPath());
			if (option.isEmpty()) {
				throw new ResponseException(Error.DATA_NOT_FOUND, api.getApiPath());
			}
		}

		apiList = item.getDeniedApiList();
		for (AuthApi api : apiList) {
			Optional<ApiMst> option = apiRepository.findByApiPath(api.getApiPath());
			if (option.isEmpty()) {
				throw new ResponseException(Error.DATA_NOT_FOUND, api.getApiPath());
			}
		}

		Optional<UserGroupApi> option = userGroupApiRepository.findByUserGroupId(item.getUserGroupId());
		if (option.isEmpty()) {
			item.setId(0L);
		} else {
			item.setId(option.get().getId());
		}

		userGroupApiRepository.save(item);
		return new Response();
	}

	public Response delete(UserGroupApi item) throws RuntimeException {
		if (userGroupApiRepository.existsById(item.getId())) {
			userGroupApiRepository.deleteById(item.getId());
		} else {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		return new Response();
	}
}
