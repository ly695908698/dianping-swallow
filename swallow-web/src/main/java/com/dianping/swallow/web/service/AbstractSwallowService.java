package com.dianping.swallow.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @author mingdongli
 *
 * 2015年5月20日下午2:05:16
 */
@Component
public abstract class AbstractSwallowService {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${swallow.web.env.notproduct}")
	protected boolean									showContentToAll;
	
	@Value("${swallow.web.admin.defaultadmin}")
	protected String 									defaultAdmin;

	public boolean isShowContentToAll() {
		return showContentToAll;
	}

	public void setShowContentToAll(boolean showContentToAll) {
		this.showContentToAll = showContentToAll;
	}

	public String getDefaultAdmin() {
		return defaultAdmin;
	}

	public void setDefaultAdmin(String defaultAdmin) {
		this.defaultAdmin = defaultAdmin;
	}
	

	
}
