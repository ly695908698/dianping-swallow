package com.dianping.swallow.web.alarmer.impl;

import java.util.concurrent.Future;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.impl.AbstractRetriever;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午6:06:20
 */
public abstract class AbstractStatsAlarmer extends AbstractAlarmer implements MonitorDataListener {

	protected final static String CAT_TYPE = "StatsDataAlarmer";

	protected static final String TOTAL_KEY = MonitorData.TOTAL_KEY;

	private final static long DAY_TIMESTAMP_UNIT = 24 * 60 * 60 * 1000;

	private Future<?> future;

	protected static final long TIME_SECTION = 5 * 60 / AbstractCollector.SEND_INTERVAL;

	private long lastTimeKey = -1;

	@Override
	public void doInitialize() throws Exception {
		super.doInitialize();
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
		if (future != null && !future.isCancelled()) {
			future.cancel(false);
		}
	}

	protected void doDispose() throws Exception {
		super.doDispose();
	}

	@Override
	public void achieveMonitorData() {
		logger.info("[achieveMonitorData] statsDataAlarmer {}", getClass().getSimpleName());

		future = taskManager.submit(new Runnable() {
			@Override
			public void run() {
				SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, alarmName);
				catWrapper.doAction(new SwallowAction() {
					@Override
					public void doAction() throws SwallowException {
						doAlarm();
					}
				});
			}

		});
	}

	public abstract void doAlarm();

	public long getLastTimeKey() {
		return lastTimeKey;
	}

	public void setLastTimeKey(long lastTimeKey) {
		this.lastTimeKey = lastTimeKey;
	}

	protected long getPreNDayKey(int n, long timespan) {
		return System.currentTimeMillis() - n * DAY_TIMESTAMP_UNIT - timespan;
	}

	protected long getTimeKey(long timeMillis) {
		return AbstractRetriever.getKey(timeMillis);
	}

	protected static long getPreDayKey(long timeKey) {
		return timeKey - AbstractRetriever.getKey(DAY_TIMESTAMP_UNIT).longValue();
	}

	protected static final long getTimeSection() {
		return TIME_SECTION;
	}

}
