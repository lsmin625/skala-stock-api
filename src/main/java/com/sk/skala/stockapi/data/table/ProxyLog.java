package com.sk.skala.stockapi.data.table;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "bff_proxy_log")
public class ProxyLog {
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private long timeStamp;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date requestTime;

	private String clientAddress;
	private String proxyTid;
	private String bffUserId;
	private String apiMethod;
	private String apiPath;
	private String apiResult;
	private String requestParams;
	private String requestBody;
	private String responseCode;
	private String responseBody;
	private long elapsedTime;

}
