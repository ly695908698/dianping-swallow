package com.dianping.swallow.web.controller.dto;

public class SendMessageDto {

	private String topic;

	private String content;

	private String type;

	private String delimitor;

	private String property;

	private String authentication;

	public String getTopic() {
		return topic;
	}

	public String getContent() {
		return content;
	}

	public String getType() {
		return type;
	}

	public String getDelimitor() {
		return delimitor;
	}

	public String getProperty() {
		return property;
	}

	public String getAuthentication() {
		return authentication;
	}
}
