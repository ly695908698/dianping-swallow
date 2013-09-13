package com.dianping.swallow.common.internal.whitelist;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;

/**
 * 使用时需要注入dynamicConfig，并调用init方法初始化
 * 
 * @author kezhu.wu
 * 
 */
public class WhiteList implements ConfigChangeListener {

    private static final String TOPIC_SPLIT      = ";";

    private static final Logger LOG              = LoggerFactory.getLogger(WhiteList.class);

    private static final String TOPIC_WHITE_LIST = "swallow.topic.whitelist";

    private Set<String>         topics;

    private DynamicConfig       dynamicConfig;

    public void init() {
        build();

        //监听lion
        dynamicConfig.addConfigChangeListener(this);

    }

    public boolean isValid(String topic) {
        return topics != null && topics.contains(topic);
    }

    @Override
    public void onConfigChange(String key, String value) {
        LOG.info("Invoke onConfigChange, key='" + key + "', value='" + value + "'");
        key = key.trim();
        if (key.equals(TOPIC_WHITE_LIST)) {
            try {
                build();
            } catch (RuntimeException e) {
                LOG.error("Error initialize 'topic white list' from lion ", e);
            }
        }
    }

    private void build() {
        String value = dynamicConfig.get(TOPIC_WHITE_LIST);

        Set<String> _topics = new HashSet<String>();

        if (value != null && value.length() > 0) {
            value = value.trim();
            String[] topics = value.split(TOPIC_SPLIT);
            for (String t : topics) {
                if (!"".equals(t.trim())) {
                    _topics.add(t);
                }
            }
        }

        this.topics = _topics;

        LOG.info("White list topic is :" + topics);
    }

    public void setDynamicConfig(DynamicConfig dynamicConfig) {
        this.dynamicConfig = dynamicConfig;
    }

}
