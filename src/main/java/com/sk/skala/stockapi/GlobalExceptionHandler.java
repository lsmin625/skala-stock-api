package com.sk.skala.stockapi;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.Response;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(value = Exception.class)
	public @ResponseBody Response takeException(Exception e) {
		Response response = new Response();
		response.setError(Error.SYSTEM_ERROR.getCode(), e.getMessage());
		log.error("GlobalExceptionHandler.Exception: {}", e.getMessage());
		return response;
	}

	@ExceptionHandler(value = NullPointerException.class)
	public @ResponseBody Response takeNullPointerException(Exception e) {
		Response response = new Response();
		response.setError(Error.SYSTEM_ERROR.getCode(), e.getMessage());
		log.error("GlobalExceptionHandler.NullPointerException: {}", e.getMessage());
		e.printStackTrace();
		return response;
	}

}