package com.sk.skala.stockapi.service;

import java.util.Random;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sk.skala.stockapi.config.Constant;
import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.AccountSession;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.request.AuthRequest;
import com.sk.skala.stockapi.data.request.MaskingRequest;
import com.sk.skala.stockapi.data.response.AuthResponse;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.tools.JsonTool;
import com.sk.skala.stockapi.tools.StringTool;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final StringRedisTemplate stringRedisTemplate;

	public Response sendCode(AuthRequest request) throws Exception {
		if (StringTool.isAnyEmpty(request.getCustomerId(), request.getCustomerPhone())) {
			throw new ParameterException("customerId", "customerPhone");
		}

		Random random = new Random();
		String authCode = Integer.toString(100000 + random.nextInt(900000));

		AuthResponse authResponse = new AuthResponse(request);
		authResponse.setAuthCode(authCode);

		stringRedisTemplate.opsForValue().set(Constant.MASKING_AUTHCODE_REDIS + request.getCustomerId() + authCode,
				JsonTool.toString(authResponse), Constant.DURATION_AUTHCODE);

		Response response = new Response();
		response.setBody(authResponse);

		return response;
	}

	public Response unlockMasking(MaskingRequest request) throws Exception {
		if (StringTool.isAnyEmpty(request.getCustomerId(), request.getAuthCode())) {
			throw new ParameterException("customerId", "authCode");
		}

		String userId = getSessionUserId();
		if (StringTool.isAnyEmpty(userId)) {
			throw new ResponseException(Error.SESSION_NOT_FOUND);
		}
		request.setUserId(userId);

		String authCodeKey = Constant.MASKING_AUTHCODE_REDIS + request.getCustomerId() + request.getAuthCode();
		Boolean hasKey = stringRedisTemplate.hasKey(authCodeKey);
		if (Boolean.FALSE.equals(hasKey)) {
			throw new ResponseException(Error.INVALID_AUTH_CODE);
		}
		stringRedisTemplate.delete(authCodeKey);

		stringRedisTemplate.opsForValue().set(Constant.MASKING_UNLOCK_REDIS + userId + request.getCustomerId(),
				JsonTool.toString(request), Constant.DURATION_MASKING);

		return new Response();
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
