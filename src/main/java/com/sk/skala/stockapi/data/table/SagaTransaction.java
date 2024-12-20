package com.sk.skala.stockapi.data.table;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "bff_saga_mst")
public class SagaTransaction extends Auditable<String> {
	@Id
	private String sagaId;

	private String sagaName;

	@JsonIgnore
	private String backendApis;

	public List<BackendApi> getBackendApiList() {
		if (this.backendApis != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				return objectMapper.readValue(this.backendApis, new TypeReference<List<BackendApi>>() {
				});
			} catch (Exception e) {
				throw new RuntimeException("Failed to parse backendApis JSON object", e);
			}
		} else {
			return new ArrayList<BackendApi>();
		}
	}

	public void setBackendApiList(List<BackendApi> backendApiList) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			this.backendApis = objectMapper.writeValueAsString(backendApiList);
		} catch (Exception e) {
			throw new RuntimeException("Failed to serialize backendApiList to JSON string", e);
		}
	}
}
