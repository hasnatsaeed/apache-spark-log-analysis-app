package com.log.analysis.model;

import java.io.Serializable;

public class ServerEnpointAccessCountModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String endpoint;
	private Long count;
	
	
	public ServerEnpointAccessCountModel() {
		super();
	}
	public ServerEnpointAccessCountModel(String endpoint, Long count) {
		super();
		this.endpoint = endpoint;
		this.count = count;
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	
	
}
