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
import com.sk.skala.stockapi.data.table.BackendApi;
import com.sk.skala.stockapi.data.table.SagaTransaction;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.repository.SagaRepository;
import com.sk.skala.stockapi.tools.StringTool;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SagaTransactionService {
	private final SagaRepository sagaRepository;
	private final StringRedisTemplate stringRedisTemplate;

	public Response getSagaTransactions(String keyword, int offset, int count) throws Exception {
		String wildcard = StringTool.like(keyword);
		Pageable pageable = PageRequest.of(offset, count);
		Page<SagaTransaction> paged = sagaRepository.findBySagaNameLike(wildcard, pageable);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setOffset(pageable.getPageNumber());
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response getSagaTransactionById(String sagaId) throws Exception {
		if (StringTool.isEmpty(sagaId)) {
			throw new ParameterException("sagaId is empty");
		}

		Optional<SagaTransaction> option = sagaRepository.findById(sagaId);
		if (option.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND, "sagaId=" + sagaId);
		}

		Response response = new Response();
		response.setBody(option.get());
		return response;
	}

	public Response saveSagaTransaction(SagaTransaction sagaTransaction) throws Exception {
		if (StringTool.isAnyEmpty(sagaTransaction.getSagaId(), sagaTransaction.getSagaName())) {
			throw new ParameterException("SAGA ID or Name is empty");
		}

		List<BackendApi> list = sagaTransaction.getBackendApiList();
		if (list.size() <= 1) {
			throw new ParameterException("Backend APIs should be at least two or more");
		}
		for (BackendApi api : list) {
			if (StringTool.isAnyEmpty(api.getName(), api.getUrl())) {
				throw new ParameterException("API Name or URL is empty");
			}
		}

		sagaRepository.save(sagaTransaction);
		stringRedisTemplate.delete(Constant.SAGA_TRANSACTION_REDIS + sagaTransaction.getSagaId());
		return new Response();
	}

	public Response deleteSagaTransaction(String sagaId) throws Exception {
		if (!sagaRepository.existsById(sagaId)) {
			throw new ResponseException(Error.SAGA_TRANSACTION_IVALID, "sagaId=" + sagaId);
		}

		sagaRepository.deleteById(sagaId);
		stringRedisTemplate.delete(Constant.SAGA_TRANSACTION_REDIS + sagaId);
		return new Response();
	}
}
