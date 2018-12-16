package com.log.analysis.model;

import java.io.Serializable;
import java.util.List;

public class LogAnalysisResultModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<HttpResponseCodeCountModel> httpResponseCodeCountAnalysis;
	
	private List<ServerEnpointAccessCountModel> serverEndpointAccessCountAnalysis;


	public List<HttpResponseCodeCountModel> getHttpResponseCodeCountAnalysis() {
		return httpResponseCodeCountAnalysis;
	}

	public void setHttpResponseCodeCountAnalysis(List<HttpResponseCodeCountModel> httpResponseCodeCountAnalysis) {
		this.httpResponseCodeCountAnalysis = httpResponseCodeCountAnalysis;
	}

	public List<ServerEnpointAccessCountModel> getServerEndpointAccessCountAnalysis() {
		return serverEndpointAccessCountAnalysis;
	}

	public void setServerEndpointAccessCountAnalysis(
			List<ServerEnpointAccessCountModel> serverEndpointAccessCountAnalysis) {
		this.serverEndpointAccessCountAnalysis = serverEndpointAccessCountAnalysis;
	}
	
	
	

}
