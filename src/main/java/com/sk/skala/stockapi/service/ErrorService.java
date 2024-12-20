package com.sk.skala.stockapi.service;

import org.springframework.stereotype.Service;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;

@Service
public class ErrorService {

	public Response getException() throws Exception {
		throw new Exception("hello! system error");
	}

	public Response getParameterException() throws Exception {
		throw new ParameterException("id", "name");
	}

	public Response getResponseException() throws Exception {
		throw new ResponseException(Error.DATA_NOT_FOUND);
	}

}
