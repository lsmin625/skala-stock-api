package com.sk.skala.stockapi.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.SagaTransaction;
import com.sk.skala.stockapi.service.SagaExecutionService;
import com.sk.skala.stockapi.service.SagaHistoryService;
import com.sk.skala.stockapi.service.SagaTransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/saga")
public class SagaController {

	private final SagaTransactionService sagaTransactionService;
	private final SagaExecutionService sagaExecutionService;
	private final SagaHistoryService sagaHistoryService;

	@GetMapping("/transaction/list")
	public Response getList(@RequestParam(defaultValue = "") String keyword,
			@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int count)
			throws Exception {
		return sagaTransactionService.getSagaTransactions(keyword, offset, count);
	}

	@GetMapping("/transaction")
	public Response get(@RequestParam String id) throws Exception {
		return sagaTransactionService.getSagaTransactionById(id);
	}

	@PostMapping("/transaction")
	public Response update(@RequestBody SagaTransaction sagaTransaction) throws Exception {
		return sagaTransactionService.saveSagaTransaction(sagaTransaction);
	}

	@DeleteMapping("/transaction")
	public Response delete(@RequestBody SagaTransaction sagaTransaction) throws Exception {
		return sagaTransactionService.deleteSagaTransaction(sagaTransaction.getSagaId());
	}

	@PostMapping("/execute/{id}/**")
	public Response executeSagaTransaction(@PathVariable String id, @RequestBody Map<String, Object> requestBody)
			throws Exception {
		return sagaExecutionService.execute(id, requestBody);
	}

	@GetMapping("/history/list")
	public Response getHistoryList(@RequestParam String sagaId, @RequestParam(defaultValue = "0") int offset,
			@RequestParam(defaultValue = "10") int count) throws Exception {
		return sagaHistoryService.getSagaTransactions(sagaId, offset, count);
	}
}
