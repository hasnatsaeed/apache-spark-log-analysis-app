package com.log.analysis.model;

import java.io.Serializable;

public class ClientAccessCountModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String clientName;
	private Long count;
	
	
	public ClientAccessCountModel() {
		super();
	}
	public ClientAccessCountModel(String clientName, Long count) {
		super();
		this.clientName = clientName;
		this.count = count;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	
	
	
}
