package com.dianping.swallow.web.monitor.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowCallableWrapper;
import com.dianping.swallow.common.internal.action.impl.CatCallableWrapper;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.AbstractAllData;
import com.dianping.swallow.common.server.monitor.data.statis.ProducerAllData;
import com.dianping.swallow.common.server.monitor.data.statis.ProducerServerStatisData;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerServerData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerTopicData;
import com.dianping.swallow.web.dao.ProducerMonitorDao;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.StatsDataDesc;
import com.dianping.swallow.web.service.ProducerServerStatsDataService;
import com.dianping.swallow.web.service.ProducerTopicStatsDataService;

/**
 * @author mengwenchao
 *
 *         2015年4月21日 上午11:04:09
 */
@Component
public class DefaultProducerDataRetriever
		extends
		AbstractMonitorDataRetriever<ProducerTopicData, ProducerServerData, ProducerServerStatisData, ProducerMonitorData>
		implements ProducerDataRetriever {

	public static final String CAT_TYPE = "DefaultProducerDataRetriever";

	@Autowired
	private ProducerServerStatsDataService pServerStatsDataService;

	@Autowired
	private ProducerTopicStatsDataService pTopicStatsDataService;

	@Autowired
	private ProducerMonitorDao producerMonitorDao;

	@Override
	public StatsData getSaveDelay(String topic, long start, long end) {

		if (dataExistInMemory(start, end)) {
			return getDelayInMemory(topic, StatisType.SAVE, start, end);
		}

		return getDelayInDb(topic, StatisType.SAVE, start, end);
	}
	
	@Override
	protected StatsData getDelayInDb(String topic, StatisType type, long start, long end) {
		
		long startKey = getKey(start);
		long endKey = getKey(end);
		
		NavigableMap<Long, Long> rawData = pTopicStatsDataService.findSectionDelayData(topic, startKey, endKey);
		rawData = fillStatsData(rawData, startKey, endKey);
		return createStatsData(createQpxDesc(topic, StatisType.SAVE), rawData, start, end);
	}

	@Override
	public StatsData getQpx(String topic, QPX qpx, long start, long end) {

		if (dataExistInMemory(start, end)) {
			return getQpxInMemory(topic, StatisType.SAVE, start, end);
		}
		return getQpxInDb(topic, StatisType.SAVE, start, end);
	}

	@Override
	protected StatsData getQpxInDb(String topic, StatisType type, long start, long end) {
		long startKey = getKey(start);
		long endKey = getKey(end);
		
		NavigableMap<Long, Long> rawData = pTopicStatsDataService.findSectionQpsData(topic, startKey, endKey);
		rawData = fillStatsData(rawData, startKey, endKey);
		return createStatsData(createQpxDesc(topic, type), rawData, start, end);
	}

	@Override
	public Map<String, StatsData> getServerQpx(QPX qpx, long start, long end) {

		if (dataExistInMemory(start, end)) {
			return getServerQpxInMemory(qpx, StatisType.SAVE, start, end);
		}

		return getServerQpxInDb(qpx, StatisType.SAVE, start, end);
	}

	protected Map<String, StatsData> getServerQpxInDb(QPX qpx, StatisType type, long start, long end) {
		Map<String, StatsData> result = new HashMap<String, StatsData>();

		long startKey = getKey(start);
		long endKey = getKey(end);
		Map<String, NavigableMap<Long, Long>> statsDataMaps = pServerStatsDataService.findSectionQpsData(startKey,
				endKey);

		for (Map.Entry<String, NavigableMap<Long, Long>> statsDataMap : statsDataMaps.entrySet()) {
			String serverIp = statsDataMap.getKey();

			if (StringUtils.equals(TOTAL_KEY, serverIp)) {
				continue;
			}

			NavigableMap<Long, Long> statsData = statsDataMap.getValue();
			statsData = fillStatsData(statsData, startKey, endKey);
			result.put(serverIp, createStatsData(createServerQpxDesc(serverIp, type), statsData, start, end));

		}
		return result;
	}

	@Override
	public StatsData getQpx(String topic, QPX qpx) {

		return getQpx(topic, qpx, getDefaultStart(), getDefaultEnd());
	}

	@Override
	public StatsData getSaveDelay(final String topic) throws Exception {

		SwallowCallableWrapper<StatsData> wrapper = new CatCallableWrapper<StatsData>(CAT_TYPE, "getSaveDelay");

		return wrapper.doCallable(new Callable<StatsData>() {

			@Override
			public StatsData call() throws Exception {

				return getSaveDelay(topic, getDefaultStart(), getDefaultEnd());
			}
		});
	}

	@Override
	protected AbstractAllData<ProducerTopicData, ProducerServerData, ProducerServerStatisData, ProducerMonitorData> createServerStatis() {

		return new ProducerAllData();
	}

	@Override
	public Map<String, StatsData> getServerQpx(QPX qpx) {
		return getServerQpx(qpx, getDefaultStart(), getDefaultEnd());
	}

	@Override
	protected StatsDataDesc createDelayDesc(String topic, StatisType type) {

		return new ProducerStatsDataDesc(topic, type.getDelayDetailType());
	}

	@Override
	protected StatsDataDesc createQpxDesc(String topic, StatisType type) {

		return new ProducerStatsDataDesc(topic, type.getQpxDetailType());
	}

	@Override
	protected StatsDataDesc createServerQpxDesc(String serverIp, StatisType type) {

		return new ProducerServerDataDesc(serverIp, MonitorData.TOTAL_KEY, type.getQpxDetailType());
	}

	@Override
	protected StatsDataDesc createServerDelayDesc(String serverIp, StatisType type) {

		return new ProducerServerDataDesc(serverIp, MonitorData.TOTAL_KEY, type.getDelayDetailType());
	}

}