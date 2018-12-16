package com.log.analysis.model;

import java.io.Serializable;

public class HttpResponseCodeCountModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String responseCode;

	private Long count;	
	
	

	public HttpResponseCodeCountModel() {
		super();
	}

	public HttpResponseCodeCountModel(String responseCode, Long count) {
		super();
		this.responseCode = responseCode;
		this.count = count;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
	
	

}
