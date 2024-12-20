package com.sk.skala.stockapi.data.table;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.tools.JsonTool;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "bff_saga_log")
public class SagaHistory extends Auditable<String> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String sagaId;
	private String sagaName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	Date sagaTime;

	private String apiName;
	private String apiUrl;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	Date startTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	Date endTime;

	private String result;
	private String requestBody;
	private String responseBody;

	public SagaHistory() {
	}

	public SagaHistory(SagaTransaction sagaTransaction, BackendApi backendApi, Map<String, Object> requestBody) {
		this.sagaId = sagaTransaction.getSagaId();
		this.sagaName = sagaTransaction.getSagaName();
		this.apiName = backendApi.getName();
		this.apiUrl = backendApi.getUrl();
		this.startTime = new Date();
		this.requestBody = JsonTool.toString(requestBody);
	}

	public void setResponse(Response response) {
		if (Response.SUCCESS == response.getResult()) {
			this.result = "SUCCESS";
		} else {
			this.result = "FAIL";
		}
		this.endTime = new Date();
		this.responseBody = JsonTool.toString(response);
	}
}
