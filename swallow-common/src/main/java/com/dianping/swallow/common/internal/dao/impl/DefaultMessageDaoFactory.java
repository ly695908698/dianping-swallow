package com.dianping.swallow.common.internal.dao.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.FactoryBean;

import com.dianping.swallow.common.internal.config.SwallowServerConfig;
import com.dianping.swallow.common.internal.config.TopicConfig;
import com.dianping.swallow.common.internal.config.impl.AbstractSwallowServerConfig;
import com.dianping.swallow.common.internal.config.impl.AbstractSwallowServerConfig.SwallowConfigArgs;
import com.dianping.swallow.common.internal.dao.Cluster;
import com.dianping.swallow.common.internal.dao.ClusterManager;
import com.dianping.swallow.common.internal.dao.DAO;
import com.dianping.swallow.common.internal.dao.DAOContainer;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.common.internal.observer.Observable;
import com.dianping.swallow.common.internal.observer.Observer;
import com.dianping.swallow.common.internal.util.StringUtils;

/**
 * @author mengwenchao
 *
 * 2015年11月3日 下午3:43:03
 */
public class DefaultMessageDaoFactory extends AbstractLifecycle implements FactoryBean<MessageDAO<?>>, InvocationHandler, Observer{
	
	public static final int ORDER = ClusterManager.ORDER + 1;

	protected final Logger logger     = LogManager.getLogger(getClass());
	
	private Map<String, DAOContainer<MessageDAO<?>>> daos = new ConcurrentHashMap<String, DAOContainer<MessageDAO<?>>>();
	
	private SwallowServerConfig swallowServerConfig;
	
	private ClusterManager clusterManager;
	
	@Override
	public MessageDAO<?> getObject() throws Exception {
		
		if(logger.isInfoEnabled()){
			logger.info("[getObject]");
		}
		
		return (MessageDAO<?>) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ MessageDAO.class }, this);
	}

	
	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();

		swallowServerConfig.addObserver(this);
		loadSwallowConfig();
	}
	
	
	@Override
	protected void doDispose() throws Exception {

		super.doDispose();
	}
	
	private void loadSwallowConfig() {

		try {
			createAllTopicDao();
		} catch (Exception e) {
			throw new IllegalArgumentException("[loadSwallowConfig]", e);
		}
	}

	public void createAllTopicDao() {
		
		
		Map<String, DAOContainer<MessageDAO<?>>> daos = new ConcurrentHashMap<String, DAOContainer<MessageDAO<?>>>();
		
		for (String topicName : swallowServerConfig.getCfgTopics()){

			try{
				MessageDAO<?> messageDAO = createDao(topicName);
				if(messageDAO != null){
					daos.put(topicName, new SingleDaoContainer<MessageDAO<?>>(messageDAO));
				}
			} catch (ClusterCreateException e) {
				logger.error("[createAllTopicDao]" + topicName, e);
			}
		}

		if(daos.get(AbstractSwallowServerConfig.TOPICNAME_DEFAULT) == null){
			throw new IllegalStateException("default topic not exist!!" + daos.keySet());
		}
		
		this.daos = daos;
		
		if(logger.isInfoEnabled()){
			logger.info("[createAllTopicDao]" + daos);
		}
			
	}
	
	private void deleteTopicDao(String topicName) {
		
		if(AbstractSwallowServerConfig.TOPICNAME_DEFAULT.equals(topicName)){
			throw new IllegalArgumentException("default topic can not be deleted!!" + topicName);
		}
		
		DAOContainer<MessageDAO<?>> daoContainer = daos.remove(topicName);
		
		if(logger.isInfoEnabled()){
			logger.info("[deleteTopicDao][" + topicName + "]" + daoContainer);
		}
	}

	@Override
	public void update(Observable observable, Object rawArgs) {

		SwallowConfigArgs args = (SwallowConfigArgs) rawArgs;
		
		if(logger.isInfoEnabled()){
			logger.info("[update]" + args);
		}
		
		switch (args.getItem()) {

		case ALL_TOPIC_STORE_MAPPING:
			createAllTopicDao();
			break;
		case TOPIC_STORE:
			
			switch(args.getBehavior()){
			
				case ADD:
					createOrUpdateDao(args.getTopic(), false);
					break;
				case UPDATE:
					if(configChanges(args.getTopic(), args.getOldConfig())){
						createOrUpdateDao(args.getTopic(), true);
					}
					break;
				case DELETE:
					deleteTopicDao(args.getTopic());
					break;
				default:
					logger.warn("[update][unknown behavior]" + args.getBehavior());
			}
			break;
		default:
			logger.warn("[update][unknown item]" + args);
		}
	}

	
	private boolean configChanges(String topicName, TopicConfig oldConfig) {
		
		TopicConfig newConfig =  swallowServerConfig.getTopicConfig(topicName);
		
		boolean result = true;
		
		String newStore = StringUtils.trimToNull(newConfig.getStoreUrl());
		String oldStore = oldConfig == null ? null : StringUtils.trimToNull(oldConfig.getStoreUrl());
		
		if(StringUtils.isEmpty(newStore) && StringUtils.isEmpty(oldStore)){
			result = false;
		}else if(!StringUtils.isEmpty(newStore)  && !StringUtils.isEmpty(oldStore) && oldStore.equalsIgnoreCase(newStore)){
			result  = false;
		}
		
		if(!result){
			if(logger.isInfoEnabled()){
				logger.info("[configChanges][config not changed!!]" + oldConfig + "," + newConfig);
			}
		}
		
		return result;
	}


	@Override
	public Class<?> getObjectType() {
		return MessageDAO.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		if(method.getName().equals(DAO.GET_CLUSTER)){
			return null;
		}
		
		if(args.length == 0){
			throw new IllegalArgumentException("wrong argument:" + args.length);
		}
		
		String topicName = (String) args[0];

		DAOContainer<MessageDAO<?>> dao = getDaoContainer(topicName);
	
		try{
			return method.invoke(dao.getDao(), args);
		}catch(InvocationTargetException e){
			throw e.getCause();
		}
	}
	
	private DAOContainer<MessageDAO<?>> getDaoContainer(String topicName) {
		
		DAOContainer<MessageDAO<?>> daoContainer = daos.get(topicName);
		if(daoContainer != null){
			return daoContainer;
		}
		
		return daos.get(AbstractSwallowServerConfig.TOPICNAME_DEFAULT);
	}
	
	
	private synchronized void createOrUpdateDao(String topicName, boolean isUpdate) {

		if(logger.isInfoEnabled()){
			logger.info("[createOrUpdateDao]" + topicName);
		}
		
		DAOContainer<? extends MessageDAO<?>> daoContainer = daos.get(topicName);
		if(daoContainer == null && isUpdate){
			logger.warn("[createOrUpdateDao][update]" + topicName + ", old not exist");
		}
		
		if(daoContainer != null && !isUpdate){
			logger.warn("[createOrUpdateDao][createNew]" + topicName + ", old exist");
		}
		
		try {
						
			MessageDAO<?> messageDAO = createDao(topicName);
			if(messageDAO == null){
				if(logger.isInfoEnabled()){
					logger.info("[createOrUpdateDao][dao null]" + topicName);
				}
				
				if(daoContainer != null){
					
					if(logger.isInfoEnabled()){
						logger.info("[createOrUpdateDao][old not null, remove that!]" + topicName);
					}
					deleteTopicDao(topicName);
				}
				return;
			}
			
			DAOContainer<MessageDAO<?>> result = null;
			if(daoContainer == null){
				result = new SingleDaoContainer<MessageDAO<?>>(messageDAO);
			}else{
				
				if(messageDAO.getCluster().sameCluster(daoContainer.getDao().getCluster())){
					if(logger.isInfoEnabled()){
						logger.info("[createOrUpdateDao][cluster not change!]");
					}
					return;
				}
				
				if(AbstractSwallowServerConfig.TOPICNAME_DEFAULT.equals(topicName)){
					throw new UnsupportedOperationException("can not update default topic, not supported!!" + topicName + "," + messageDAO);
				}
				
				result = new ExchangeDaoContainer<MessageDAO<?>>(daoContainer.getDao(), messageDAO);
			}
			daos.put(topicName, result);
		} catch (ClusterCreateException e) {
			logger.error("[createOrUpdateDao]" + topicName, e);
		}
		
	}
		
	private MessageDAO<?> createDao(String topicName) throws ClusterCreateException {
		
		if(logger.isInfoEnabled()){
			logger.info("[createDao]" + topicName);
		}
		
		TopicConfig topicConfig = swallowServerConfig.getTopicConfig(topicName);
		
		
		if(topicConfig == null || StringUtils.isEmpty(topicConfig.getStoreUrl())){
			return null;
		}
		
		Cluster cluster = clusterManager.getCluster(topicConfig.getStoreUrl());
		
		MessageDAO<?> messageDao = cluster.createMessageDao();
	
		return messageDao;
	}


	public Object getStatus() {
		
		Map<String, String> result = new HashMap<String, String>();
		
		for(Entry<String, DAOContainer<MessageDAO<?>>> entry : daos.entrySet()){
			
			String topic = entry.getKey();
			DAOContainer<MessageDAO<?>> daoContainer= entry.getValue();
			result.put(topic, daoContainer.toString());
		}

		return result;
	}

	public void setClusterManager(ClusterManager clusterManager) {
		this.clusterManager = clusterManager;
	}

	public void setSwallowServerConfig(SwallowServerConfig swallowServerConfig) {
		this.swallowServerConfig = swallowServerConfig;
	}
	
	/**
	 * for unit test
	 * @return
	 */
	protected Map<String, DAOContainer<MessageDAO<?>>> getDaos(){
		
		return daos;
	}
	
	@Override
	public int getOrder() {
		return ORDER;
	}
}
