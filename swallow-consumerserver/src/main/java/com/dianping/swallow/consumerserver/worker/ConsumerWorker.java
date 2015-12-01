package com.dianping.swallow.consumerserver.worker;


import io.netty.channel.Channel;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.consumer.ACKHandlerType;
import com.dianping.swallow.common.internal.lifecycle.Lifecycle;

public interface ConsumerWorker extends Lifecycle{
	
	
	/**
	 * 发送消息，如果有消息可以发送，返回true，否则，返回false
	 * @return
	 */
	boolean  sendMessage();

   /**
     * 处理greet信息
	 * @param channel
	 * @param clientThreadCount
	 * @param messageFilter
	 */
	void handleGreet(Channel channel, int clientThreadCount, MessageFilter messageFilter);

   /**
    * 处理ack信息
    * 
    * @param channel
    * @param ackedMsgId 客户端返回的messageId
    * @param type 接收到ack后的处理类型类型为{@link ACKHandlerType}
    */
   void handleAck(Channel channel, long ackedMsgId, ACKHandlerType type);

   /**
    * channel断开时所做的操作
    * 
    * @param channel
    */
   void handleChannelDisconnect(Channel channel);

   /**
    * 判断同consumerId下的所有的连接是否都不存在
    * 
    * @return
    */
   boolean allChannelDisconnected();


   void recordAck();

   void handleHeartBeat(Channel channel);
   
}
