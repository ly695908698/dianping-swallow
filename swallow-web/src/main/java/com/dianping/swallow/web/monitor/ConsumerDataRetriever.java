package com.dianping.swallow.web.monitor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年4月21日 上午10:38:07
 */
public interface ConsumerDataRetriever extends MonitorDataRetriever {

    boolean dataExistInMemory(CasKeys keys, long start, long end);

    List<OrderStatsData> getOrderForAllConsumerId(int size);

    List<OrderStatsData> getOrderForAllConsumerId(int size, long start, long end);

    List<ConsumerDataPair> getDelayForAllConsumerId(String topic, long start, long end);

    List<ConsumerDataPair> getDelayForAllConsumerId(String topic) throws Exception;

    List<ConsumerDataPair> getQpxForAllConsumerId(String topic, QPX qpx, long start, long end);

    List<ConsumerDataPair> getQpxForAllConsumerId(String topic, QPX qpx);

    Map<String, ConsumerDataPair> getServerQpx(QPX qpx, long start, long end);

    Map<String, ConsumerDataPair> getServerQpx(QPX qpx);

    Map<String, Set<String>> getAllTopics();

    Map<String, ConsumerDataPair> getAllIpQpx(String topic, String consumerId, long start, long end);

    Map<String, ConsumerDataPair> getAllIpQpx(String topic, String consumerId);

    List<IpStatsData> getAllIpQpxList(String topic, String consumerId, long start, long end);

    List<IpStatsData> getAllIpQpxList(String topic, String consumerId);

    Map<String, ConsumerDataPair> getAllIpDelay(String topic, String consumerId, long start, long end);

    Map<String, ConsumerDataPair> getAllIpDelay(String topic, String consumerId);

    List<IpStatsData> getAllIpDelayList(String topic, String consumerId, long start, long end);

    List<IpStatsData> getAllIpDelayList(String topic, String consumerId);


    public static class ConsumerDataPair extends AbstractStatsData {

        private String consumerId;
        private StatsData sendData;
        private StatsData ackData;

        public ConsumerDataPair(String consumerId, StatsData sendData, StatsData ackData) {
            this.consumerId = consumerId;
            this.sendData = sendData;
            this.ackData = ackData;
        }

        public String getConsumerId() {
            return consumerId;
        }

        public StatsData getSendData() {
            return sendData;
        }

        public StatsData getAckData() {
            return ackData;
        }
    }

    public static class ConsumerOrderDataPair {

        private OrderStatsData sendStatsData;

        private OrderStatsData ackStatsData;

        public ConsumerOrderDataPair() {

        }

        public ConsumerOrderDataPair(OrderStatsData sendStatsData, OrderStatsData ackStatsData) {
            this.sendStatsData = sendStatsData;
            this.ackStatsData = ackStatsData;
        }

        public OrderStatsData getSendStatsData() {
            return sendStatsData;
        }

        public void setSendStatsData(OrderStatsData sendStatsData) {
            this.sendStatsData = sendStatsData;
        }

        public OrderStatsData getAckStatsData() {
            return ackStatsData;
        }

        public void setAckStatsData(OrderStatsData ackStatsData) {
            this.ackStatsData = ackStatsData;
        }
    }

}
