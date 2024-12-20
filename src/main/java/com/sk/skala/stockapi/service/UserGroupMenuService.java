package com.sk.skala.stockapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.PagedList;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.UserGroupMenu;
import com.sk.skala.stockapi.data.table.UserGroupMenuId;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.repository.UserGroupMenuRepository;
import com.sk.skala.stockapi.tools.StringTool;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserGroupMenuService {

	private final UserGroupMenuRepository userGroupMenuRepository;
	private final CodeGroupService codeGroupService;

	private static final String CDOE_GROUP_userAuthGroup = "user_auth_group";
	private static final String CDOE_GROUP_systemId = "system_id";

	public Response findAll(String userGroupId, int offset, int count) throws RuntimeException {
		if (StringTool.isEmpty(userGroupId)) {
			throw new ParameterException("userGroupId");
		}

		Pageable pageable = PageRequest.of(offset, count);
		Page<UserGroupMenu> paged = userGroupMenuRepository.findAllByUserGroupId(userGroupId, pageable);
		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setOffset(pageable.getPageNumber());
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response findAll(String userGroupId, String systemId) throws RuntimeException {
		if (StringTool.isEmpty(userGroupId)) {
			throw new ParameterException("userGroupId");
		}

		List<UserGroupMenu> items = userGroupMenuRepository.findAllByUserGroupIdAndSystemId(userGroupId, systemId);
		PagedList pagedList = new PagedList();
		pagedList.setTotal(items.size());
		pagedList.setOffset(0);
		pagedList.setCount(items.size());
		pagedList.setList(items);

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response find(UserGroupMenu req) throws RuntimeException {
		if (StringTool.isAnyEmpty(req.getUserGroupId(), req.getSystemId(), req.getMenuId())) {
			throw new ParameterException("userGroupId", "systemId", "menuId");
		}

		UserGroupMenuId userGroupMenuId = new UserGroupMenuId(req.getUserGroupId(), req.getSystemId(), req.getMenuId());
		Optional<UserGroupMenu> option = userGroupMenuRepository.findById(userGroupMenuId);
		if (option.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		Response response = new Response();
		response.setBody(option.get());
		return response;
	}

	public Response save(UserGroupMenu menu) throws RuntimeException {
		if (StringTool.isAnyEmpty(menu.getUserGroupId(), menu.getSystemId(), menu.getMenuId())) {
			throw new ParameterException("userGroupId", "systemId", "menuId");
		}

		if (!codeGroupService.isCodeInGroup(CDOE_GROUP_userAuthGroup, menu.getUserGroupId())) {
			throw new ResponseException(Error.CODE_MISSED_IN_CODE_GROUP, CDOE_GROUP_userAuthGroup);
		}

		if (!codeGroupService.isCodeInGroup(CDOE_GROUP_systemId, menu.getSystemId())) {
			throw new ResponseException(Error.CODE_MISSED_IN_CODE_GROUP, CDOE_GROUP_systemId);
		}

		userGroupMenuRepository.save(menu);
		return new Response();
	}

	@Transactional
	public Response saveAll(List<UserGroupMenu> menus) throws RuntimeException {
		if (menus == null || menus.size() == 0) {
			throw new ParameterException("userGroupId", "systemId", "menuId");
		}
		String userGroupId = menus.get(0).getUserGroupId();
		String systemId = menus.get(0).getSystemId();

		List<UserGroupMenu> oldList = userGroupMenuRepository.findAllByUserGroupIdAndSystemId(userGroupId, systemId);
		userGroupMenuRepository.deleteAll(oldList);
		userGroupMenuRepository.saveAll(menus);
		return new Response();
	}

	public Response delete(UserGroupMenu req) throws RuntimeException {
		UserGroupMenuId userGroupMenuId = new UserGroupMenuId(req.getUserGroupId(), req.getSystemId(), req.getMenuId());
		if (userGroupMenuRepository.existsById(userGroupMenuId)) {
			userGroupMenuRepository.deleteById(userGroupMenuId);
		} else {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		return new Response();
	}

}
