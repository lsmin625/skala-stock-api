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
@Table(name = "bff_code_group_mst")
public class CodeGroup extends Auditable<String> {
	@Id
	private String codeGroupId;

	private String codeGroupName;

	@JsonIgnore
	private String codes;

	public List<Code> getCodeList() {
		if (this.codes != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				return objectMapper.readValue(this.codes, new TypeReference<List<Code>>() {
				});
			} catch (Exception e) {
				throw new RuntimeException("Failed to parse codes JSON object", e);
			}
		} else {
			return new ArrayList<Code>();
		}
	}

	public void setCodeList(List<Code> codeList) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			this.codes = objectMapper.writeValueAsString(codeList);
		} catch (Exception e) {
			throw new RuntimeException("Failed to serialize codeList to JSON string", e);
		}
	}
}
