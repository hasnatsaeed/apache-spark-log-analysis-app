package com.log.analysis.model;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LogLineModel implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger("Access");

	private String clientIpAddress;
	private String clientId;
	private String userId;
	private String dateTime;
	private String httpMethod;
	private String serverEndpoint;
	private String httpProtocol;
	private String httpResponseCode;
	private String httpContentSize;

	private LogLineModel(String clientIpAddress, String clientId, String userId, String dateTime,
			String httpMethod, String serverEndpoint, String httpProtocol, String httpResponseCode,
			String httpContentSize) {
		this.clientIpAddress = clientIpAddress;
		this.clientId = clientId;
		this.userId = userId;
		this.dateTime = dateTime;
		this.httpMethod = httpMethod;
		this.serverEndpoint = serverEndpoint;
		this.httpProtocol = httpProtocol;
		this.httpResponseCode = httpResponseCode;
		this.httpContentSize = httpContentSize;
	}

	
	public String getClientIpAddress() {
		return clientIpAddress;
	}

	public void setClientIpAddress(String clientIpAddress) {
		this.clientIpAddress = clientIpAddress;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getServerEndpoint() {
		return serverEndpoint;
	}

	public void setServerEndpoint(String serverEndpoint) {
		this.serverEndpoint = serverEndpoint;
	}

	public String getHttpProtocol() {
		return httpProtocol;
	}

	public void setHttpProtocol(String httpProtocol) {
		this.httpProtocol = httpProtocol;
	}

	public String getHttpResponseCode() {
		return httpResponseCode;
	}

	public void setHttpResponseCode(String httpResponseCode) {
		this.httpResponseCode = httpResponseCode;
	}

	public String getHttpContentSize() {
		return httpContentSize;
	}

	public void setHttpContentSize(String httpContentSize) {
		this.httpContentSize = httpContentSize;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public static Pattern getPattern() {
		return PATTERN;
	}


	private static final String LOG_ENTRY_PATTERN =	"^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(\\S+) (\\S+) (\\S+)\" (\\d{3}) ((\\d+)|\\-) ";
	private static final Pattern PATTERN = Pattern.compile(LOG_ENTRY_PATTERN);

	public static LogLineModel parseFromLogLine(String logline) {
		Matcher m = PATTERN.matcher(logline);
		if (!m.find()) {
			logger.log(Level.ALL, "Error parsing logline => " + logline);
			return new LogLineModel("unparseable-log-entry", "12", "12", "unparseable-log-entry", "unparseable-log-entry", "unparseable-log-entry", "unparseable-log-entry", "300", "123");

		}

		return new LogLineModel(m.group(1), m.group(2), m.group(3), m.group(4), m.group(5), m.group(6),
				m.group(7), m.group(8), m.group(9));
	}

	@Override
	public String toString() {
		return String.format("%s %s %s [%s] \"%s %s %s\" %s %s", clientIpAddress, clientId, userId, dateTime,
				httpMethod, serverEndpoint, httpProtocol, httpResponseCode, httpContentSize);
	}
	
	public static String sqlTableColumnSchema() {
		return "ipAddress clientIdentd userID dateTimeString method endpoint protocol responseCode contentSize";
	}
}