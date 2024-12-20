package com.sk.skala.stockapi.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sk.skala.stockapi.config.Constant;
import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.BackendApi;
import com.sk.skala.stockapi.data.table.SagaHistory;
import com.sk.skala.stockapi.data.table.SagaTransaction;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.repository.SagaLogRepository;
import com.sk.skala.stockapi.repository.SagaRepository;
import com.sk.skala.stockapi.tools.JsonTool;
import com.sk.skala.stockapi.tools.MapTool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SagaExecutionService {

	private final SagaRepository sagaRepository;
	private final SagaLogRepository sagaLogRepository;
	private final StringRedisTemplate stringRedisTemplate;
	private RestTemplate restTemplate = new RestTemplate();

	public Response execute(String sagaId, Map<String, Object> requestBody) throws Exception {
		SagaTransaction sagaTransaction = readThroughCache(sagaId);
		if (sagaTransaction == null) {
			throw new ResponseException(Error.SAGA_TRANSACTION_IVALID);
		}

		Map<String, Object> currentBody = new HashMap<>();
		MapTool.copyProperties(requestBody, currentBody);
		List<BackendApi> successfulApis = new ArrayList<>();

		Date sagaTime = new Date();

		List<BackendApi> list = sagaTransaction.getBackendApiList();
		for (BackendApi api : list) {

			SagaHistory sagaHistory = new SagaHistory(sagaTransaction, api, currentBody);
			sagaHistory.setSagaTime(sagaTime);

			ResponseEntity<Response> responseEntity;
			try {
				responseEntity = restTemplate.postForEntity(api.getUrl(), currentBody, Response.class);
			} catch (Exception e) {
				rollback(successfulApis, currentBody);
				Response errorResponse = new Response();
				errorResponse.setError(Error.SAGA_API_ERROR);
				sagaHistory.setResponse(errorResponse);
				sagaLogRepository.save(sagaHistory);
				break;
			}

			Response apiResponse = responseEntity.getBody();

			sagaHistory.setResponse(apiResponse);
			sagaLogRepository.save(sagaHistory);

			if (apiResponse.getResult() == Response.SUCCESS) {
				if (apiResponse.getBody() != null) {
					String textBody = JsonTool.toString(apiResponse.getBody());
					MapTool.copyProperties(JsonTool.toMap(textBody), currentBody);
				}
			} else {
				rollback(successfulApis, currentBody);
				break;
			}

			successfulApis.add(api);
		}

		if (list.size() != successfulApis.size()) {
			throw new ResponseException(Error.SAGA_EXECUTION_FAILED);

		}

		Response response = new Response();
		response.setBody(successfulApis);
		return response;
	}

	private void rollback(List<BackendApi> successfulApis, Map<String, Object> requestBody) {
		for (BackendApi api : successfulApis) {
			try {
				restTemplate.postForEntity(api.getUrl() + "/cancel", requestBody, Response.class);
			} catch (Exception e) {
				log.error(e.toString());
			}
		}
	}

	private SagaTransaction readThroughCache(String sagaId) {
		String value = stringRedisTemplate.opsForValue().get(Constant.SAGA_TRANSACTION_REDIS + sagaId);
		if (value != null) {
			return JsonTool.toObject(value, SagaTransaction.class);
		}

		Optional<SagaTransaction> option = sagaRepository.findById(sagaId);
		if (!option.isEmpty()) {
			stringRedisTemplate.opsForValue().set(Constant.SAGA_TRANSACTION_REDIS + sagaId,
					JsonTool.toString(option.get()));
			return option.get();
		}

		return null;

	}
}
