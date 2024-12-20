package com.sk.skala.stockapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sk.skala.stockapi.data.common.PagedList;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.ProxyLog;
import com.sk.skala.stockapi.repository.ProxyLogRepository;
import com.sk.skala.stockapi.tools.StringTool;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class ProxyLogService {
	private final ProxyLogRepository proxyLogRespository;

	public Response findByApiUrlLike(String keyword, int offset, int count) throws Exception {
		Pageable pageable = PageRequest.of(offset, count, Sort.by(Sort.Direction.DESC, "id"));
		Page<ProxyLog> paged = proxyLogRespository.findByApiPathLike(StringTool.like(keyword), pageable);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setOffset(pageable.getPageNumber());
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response findByRequestParamsLikeOrRequestBodyLike(String keyword, int offset, int count) throws Exception {
		Pageable pageable = PageRequest.of(offset, count, Sort.by(Sort.Direction.DESC, "id"));
		Page<ProxyLog> paged = proxyLogRespository.findByRequestParamsLikeOrRequestBodyLike(StringTool.like(keyword),
				StringTool.like(keyword), pageable);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setOffset(pageable.getPageNumber());
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response findByApiResultAndApiUrlLike(String result, String keyword, int offset, int count)
			throws Exception {
		Pageable pageable = PageRequest.of(offset, count, Sort.by(Sort.Direction.DESC, "id"));
		Page<ProxyLog> paged = proxyLogRespository.findByApiResultAndApiPathLike(result, StringTool.like(keyword),
				pageable);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setOffset(pageable.getPageNumber());
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response findByResponseBodyLike(String keyword, int offset, int count) throws Exception {
		Pageable pageable = PageRequest.of(offset, count, Sort.by(Sort.Direction.DESC, "id"));
		Page<ProxyLog> paged = proxyLogRespository.findByResponseBodyLike(StringTool.like(keyword), pageable);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setOffset(pageable.getPageNumber());
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response findByElapsedTimeGreaterThan(long seconds, int offset, int count) throws Exception {
		Pageable pageable = PageRequest.of(offset, count, Sort.by(Sort.Direction.DESC, "id"));
		Page<ProxyLog> paged = proxyLogRespository.findByElapsedTimeGreaterThan(seconds * 1000, pageable);

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
