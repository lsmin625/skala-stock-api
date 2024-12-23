package com.sk.skala.stockapi.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Constant {

	// to log request or response body
	public static final String RESULT_SUCCESS = "SUCCESS";
	public static final String RESULT_FAIL = "FAIL";
	public static final Set<String> TEXT_TYPES = new HashSet<>(
			Arrays.asList("application/json", "text/plain", "text/xml"));

}
