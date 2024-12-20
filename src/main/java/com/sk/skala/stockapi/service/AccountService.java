package com.sk.skala.stockapi.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sk.skala.stockapi.config.Constant;
import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.AccountInfo;
import com.sk.skala.stockapi.data.common.AccountSession;
import com.sk.skala.stockapi.data.common.AccountSession.AuthMenu;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.User;
import com.sk.skala.stockapi.data.table.UserGroupApi;
import com.sk.skala.stockapi.data.table.UserGroupMenu;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.repository.UserGroupApiRepository;
import com.sk.skala.stockapi.repository.UserGroupMenuRepository;
import com.sk.skala.stockapi.repository.UserRepository;
import com.sk.skala.stockapi.tools.JsonTool;
import com.sk.skala.stockapi.tools.JwtTool;
import com.sk.skala.stockapi.tools.SecureTool;
import com.sk.skala.stockapi.tools.StringTool;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final Environment env;
	private final UserRepository userRepository;
	private final StringRedisTemplate stringRedisTemplate;
	private final UserGroupMenuRepository userGroupMenuRepository;
	private final UserGroupApiRepository userGroupApiRepository;
	private final CodeGroupService codeGroupService;

	private static final String CDOE_GROUP_userAuthGroup = "user_auth_group";

	public Response available(String userId) throws Exception {
		if (StringTool.isEmpty(userId)) {
			throw new ParameterException("userId");
		}

		Optional<User> option = userRepository.findById(userId);
		if (!option.isEmpty()) {
			throw new ResponseException(Error.ACCOUNT_ID_EXISTS);

		}
		return new Response();
	}

	public Response signup(AccountInfo info) throws Exception {
		if (StringTool.isAnyEmpty(info.getUserId(), info.getUserPassword(), info.getUserName())) {
			throw new ParameterException("userId", "userPassword", "userName");
		}

		if (userRepository.existsById(info.getUserId())) {
			throw new ResponseException(Error.DATA_DUPLICATED, info.getUserId());
		}
		if (isProductRuntime() && isValidPasswordPattern(info.getUserPassword())) {
			throw new ResponseException(Error.INVALID_PASSWORD_PATTERN);
		}
		User user = new User();
		user.setUserId(info.getUserId());
		user.setUserName(info.getUserName());
		user.setUserGroupId(Constant.DEFAULT_USER_GROUP_ID);
		user.setUserEmail(info.getUserEmail());
		user.setUserPhone(info.getUserPhone());
		user.setUserPassword(SecureTool.encryptAes(info.getUserPassword(), Constant.PASSWROD_RANDOM_KEY));
		user.setDtUpdatedPassword(new Date());
		userRepository.save(user);
		return new Response();
	}

	public Response updatePassword(Map<String, String> param) throws Exception {
		if (StringTool.isAnyEmpty(param.get("userPassword"), param.get("userPasswordRenew"))) {
			throw new ParameterException("userPassword", "userPasswordRenew");
		}

		String userId = getSessionUserId();
		if (StringTool.isAnyEmpty(userId)) {
			throw new ResponseException(Error.SESSION_NOT_FOUND);
		}

		Optional<User> option = userRepository.findById(userId);
		if (option.isEmpty()) {
			throw new ResponseException(Error.INVALID_ID_OR_PASSWORD);
		}
		User user = option.get();
		if (!isValidPassword(user.getUserPassword(), param.get("userPassword"))) {
			throw new ResponseException(Error.INVALID_ID_OR_PASSWORD);
		}
		if (isProductRuntime() && isValidPasswordPattern(param.get("userPasswordRenew"))) {
			throw new ResponseException(Error.INVALID_PASSWORD_PATTERN);
		}
		user.setUserPassword(SecureTool.encryptAes(param.get("userPasswordRenew"), Constant.PASSWROD_RANDOM_KEY));
		user.setDtUpdatedPassword(new Date());
		userRepository.save(user);
		return new Response();
	}

	public Response updateProfile(AccountInfo info) throws Exception {
		String userId = getSessionUserId();
		if (StringTool.isAnyEmpty(userId)) {
			throw new ResponseException(Error.SESSION_NOT_FOUND);
		}

		Optional<User> option = userRepository.findById(userId);
		if (option.isEmpty()) {
			throw new ResponseException(Error.INVALID_ID_OR_PASSWORD);
		}

		User user = option.get();
		if (!StringTool.isEmpty(info.getUserPassword())) {
			if (isProductRuntime() && isValidPasswordPattern(info.getUserPassword())) {
				throw new ResponseException(Error.INVALID_PASSWORD_PATTERN);
			}
			user.setUserPassword(SecureTool.encryptAes(info.getUserPassword(), Constant.PASSWROD_RANDOM_KEY));
			user.setDtUpdatedPassword(new Date());
		}
		user.setUserEmail(info.getUserEmail());
		user.setUserPhone(info.getUserPhone());
		userRepository.save(user);

		List<UserGroupMenu> menus = userGroupMenuRepository.findAllByUserGroupId(user.getUserGroupId());
		Optional<UserGroupApi> userGroupApiOption = userGroupApiRepository.findByUserGroupId(user.getUserGroupId());

		AccountSession accountSession = buildAccountSession(user, menus, userGroupApiOption);

		Response response = new Response();
		response.setBody(accountSession);
		return response;
	}

	public Response logout(AccountSession accountSession) throws Exception {
		stringRedisTemplate.delete(Constant.BFF_SESSION_REDIS + accountSession.getSessionId());
		stringRedisTemplate.delete(Constant.BFF_LOGIN_REDIS + accountSession.getUserId());

		return new Response();
	}

	public Response login(AccountInfo info) throws Exception {

		if (StringTool.isAnyEmpty(info.getUserId(), info.getUserPassword())) {
			throw new ParameterException("userId", "userPassword");
		}

		User user = userRepository.findById(info.getUserId())
				.orElseThrow(() -> new ResponseException(Error.INVALID_ID_OR_PASSWORD));

		if (countPasswordTry(info.getUserId()) >= Constant.PASSWORD_MAX_TRY) {
			throw new ResponseException(Error.WRONG_PASSWROD_TRIES);
		}

		if (!isValidPassword(user.getUserPassword(), info.getUserPassword())) {
			int count = countPasswordTry(info.getUserId()) + 1;
			stringRedisTemplate.opsForValue().set(Constant.BFF_PASSWORD_REDIS + info.getUserId(),
					Integer.toString(count), Constant.DURATION_PASSWORD);
			throw new ResponseException(Error.INVALID_ID_OR_PASSWORD);
		}

		List<UserGroupMenu> menus = userGroupMenuRepository.findAllByUserGroupId(user.getUserGroupId());
		Optional<UserGroupApi> userGroupApiOption = userGroupApiRepository.findByUserGroupId(user.getUserGroupId());
		AccountSession accountSession = buildAccountSession(user, menus, userGroupApiOption);
		restoreSession(accountSession);

		Response response = new Response();
		response.setBody(accountSession);
		if (isLongtimeAgo(user.getDtUpdatedPassword())) {
			response.setCode(Error.EXPIRED_PASSWROD.getCode());
			response.setMessage(Error.EXPIRED_PASSWROD.getMessage());
		}

		return response;
	}

	public Response token(AccountInfo info) throws Exception {

		if (StringTool.isAnyEmpty(info.getUserId(), info.getUserPassword())) {
			throw new ParameterException("userId", "userPassword");
		}

		User user = userRepository.findById(info.getUserId())
				.orElseThrow(() -> new ResponseException(Error.INVALID_ID_OR_PASSWORD));

		if (!isValidPassword(user.getUserPassword(), info.getUserPassword())) {
			throw new ResponseException(Error.INVALID_ID_OR_PASSWORD);
		}

		String sessionId = stringRedisTemplate.opsForValue().get(Constant.BFF_LOGIN_REDIS + info.getUserId());
		AccountSession accountSession = getSessionOrLogin(sessionId, info);
		String token = JwtTool.generateToken(sessionId, accountSession, Constant.JWT_SECRET_BFF);

		Response response = new Response();
		response.setBody(token);
		return response;
	}

	private AccountSession getSessionOrLogin(String sessionId, AccountInfo info) throws Exception {
		if (sessionId == null) {
			return (AccountSession) login(info).getBody();
		}

		String json = stringRedisTemplate.opsForValue().get(Constant.BFF_SESSION_REDIS + sessionId);
		if (json == null) {
			return (AccountSession) login(info).getBody();
		}

		return JsonTool.toObject(json, AccountSession.class);
	}

	public Response sso(String userId, String sessionId) throws Exception {
		String bffSessionId = stringRedisTemplate.opsForValue().get(Constant.BFF_LOGIN_REDIS + userId);
		if (bffSessionId == null || !bffSessionId.equals(sessionId)) {
			throw new ResponseException(Error.INVALID_SESSION_ID);
		}

		String json = stringRedisTemplate.opsForValue().get(Constant.BFF_SESSION_REDIS + sessionId);
		if (json == null) {
			throw new ResponseException(Error.INVALID_SESSION_ID);
		}

		AccountSession accountSession = JsonTool.toObject(json, AccountSession.class);
		String token = JwtTool.generateToken(accountSession.getSessionId(), accountSession, Constant.JWT_SECRET_BFF);

		Response response = new Response();
		response.setBody(token);
		return response;
	}

	private int countPasswordTry(String userId) {
		String count = stringRedisTemplate.opsForValue().get(Constant.BFF_PASSWORD_REDIS + userId);
		if (count != null) {
			return Integer.parseInt(count);
		}
		return 0;
	}

	public Response updateUserGroup(AccountInfo info) throws Exception {
		if (StringTool.isAnyEmpty(info.getUserId(), info.getUserGroupId())) {
			throw new ParameterException("userId", "userGroupId");
		}

		if (!codeGroupService.isCodeInGroup(CDOE_GROUP_userAuthGroup, info.getUserGroupId())) {
			throw new ResponseException(Error.CODE_MISSED_IN_CODE_GROUP, CDOE_GROUP_userAuthGroup);
		}

		Optional<User> option = userRepository.findById(info.getUserId());
		if (option.isPresent()) {
			User existingUser = option.get();
			existingUser.setUserGroupId(info.getUserGroupId());
			userRepository.save(existingUser);
		} else {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		return new Response();
	}

	private boolean isLongtimeAgo(Date date) {
		if (date != null) {
			if (System.currentTimeMillis() - date.getTime() > Constant.PASSWORD_LONGTIME) {
				return false;
			}
		}
		return true;
	}

	private boolean isValidPassword(String stored, String posted) throws Exception {
		if (SecureTool.decryptAes(stored, Constant.PASSWROD_RANDOM_KEY).equals(posted)) {
			return true;
		} else {
			return false;
		}
	}

	private AccountSession buildAccountSession(User user, List<UserGroupMenu> menus, Optional<UserGroupApi> option) {
		AccountSession accountSession = new AccountSession();
		accountSession.setUserId(user.getUserId());
		accountSession.setUserName(user.getUserName());
		accountSession.setUserGroupId(user.getUserGroupId());
		accountSession.setUserEmail(user.getUserEmail());
		accountSession.setUserPhone(user.getUserPhone());

		List<AuthMenu> authMenus = new ArrayList<>();
		for (UserGroupMenu menu : menus) {
			AuthMenu authMenu = new AuthMenu();
			authMenu.setSystemId(menu.getSystemId());
			authMenu.setSystemName(menu.getSystemName());
			authMenu.setMenuId(menu.getMenuId());
			authMenu.setMenuName(menu.getMenuName());
			authMenu.setAuthLevel(menu.getAuthLevel());

			if (authMenu.getAuthLevel() > 0) {
				authMenus.add(authMenu);
			}
		}
		accountSession.setAuthMenus(authMenus);

		if (option.isPresent()) {
			accountSession.setAllowedApis(option.get().getAllowedApiList());
			accountSession.setDeniedApis(option.get().getDeniedApiList());
		}

		return accountSession;
	}

	private void restoreSession(AccountSession accountSession) {
		String sessionId = stringRedisTemplate.opsForValue().get(Constant.BFF_LOGIN_REDIS + accountSession.getUserId());
		if (sessionId != null) {
			stringRedisTemplate.delete(Constant.BFF_SESSION_REDIS + sessionId);
		}
		sessionId = UUID.randomUUID().toString();
		accountSession.setSessionId(sessionId);

		stringRedisTemplate.opsForValue().set(Constant.BFF_LOGIN_REDIS + accountSession.getUserId(), sessionId);
		stringRedisTemplate.opsForValue().set(Constant.BFF_SESSION_REDIS + sessionId, JsonTool.toString(accountSession),
				Constant.DURATION_SESSION);

		Cookie cookie = new Cookie(Constant.BFF_SESSION_COOKIE, sessionId);
		cookie.setMaxAge(Constant.BFF_COOKIE_TTL);
		cookie.setPath("/");
		cookie.setSecure(isProductRuntime()); // set "https only" if product runtime

		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		attr.getResponse().addCookie(cookie);
	}

	private boolean isProductRuntime() {
		for (String profile : env.getActiveProfiles()) {
			if (profile.equalsIgnoreCase(Constant.PROFILE_PRODUCT)) {
				return true;
			}
		}
		return false;
	}

	private boolean isValidPasswordPattern(String password) {
		Pattern pattern = Pattern.compile(Constant.PASSWORD_PATTERN);
		return pattern.matcher(password).matches();
	}

	private String getSessionUserId() {
		String sessionId = null;
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (Constant.BFF_SESSION_COOKIE.equals(cookie.getName())) {
					sessionId = cookie.getValue();
					break;
				}
			}
		}

		if (sessionId != null) {
			String json = stringRedisTemplate.opsForValue().get(Constant.BFF_SESSION_REDIS + sessionId);
			if (json != null) {
				AccountSession account = JsonTool.toObject(json, AccountSession.class);
				return account.getUserId();
			}
		}

		return request.getHeader(Constant.X_BFF_USER);
	}
}
