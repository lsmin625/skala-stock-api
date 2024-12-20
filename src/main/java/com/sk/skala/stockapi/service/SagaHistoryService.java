package com.sk.skala.stockapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sk.skala.stockapi.data.common.PagedList;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.SagaHistory;
import com.sk.skala.stockapi.repository.SagaLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SagaHistoryService {
	private final SagaLogRepository sagaLogRepository;

	public Response getSagaTransactions(String sagaId, int offset, int count) throws Exception {
		Pageable pageable = PageRequest.of(offset, count, Sort.by(Sort.Direction.DESC, "id"));
		Page<SagaHistory> paged = sagaLogRepository.findBySagaId(sagaId, pageable);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setOffset(pageable.getPageNumber());
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}
}
