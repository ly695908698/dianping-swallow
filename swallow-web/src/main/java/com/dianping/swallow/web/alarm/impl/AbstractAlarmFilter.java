package com.dianping.swallow.web.alarm.impl;

import com.dianping.swallow.web.alarm.AlarmFilter;
import com.dianping.swallow.web.alarm.AlarmFilterChain;
import com.dianping.swallow.web.monitor.impl.AbstractRetriever;

/**
*
* @author qiyin
*
*/
public abstract class AbstractAlarmFilter implements AlarmFilter {

	@Override
	public boolean accept(AlarmFilterChain alarmFilterChain) {
		if(!doAccept()){
			return false;
		}else{
			return alarmFilterChain.doNext();
		}
	}
	
	public abstract boolean doAccept();
	
	protected static final long TIME_SECTION = 5 * 60 / 5;

	protected  static long getPreDayKey(long timeKey) {
		return timeKey - AbstractRetriever.getKey(24 * 60 * 60 * 1000).longValue();
	}
	
	
	protected static final long getTimeSection(){
		return TIME_SECTION;
	}

}