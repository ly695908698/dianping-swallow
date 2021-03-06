package com.dianping.swallow.common.internal.threadfactory;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * 失败时累加failCnt，sleep(min(delayBase * failCnt, delayUpperbound))，成功时清零failCnt
 * 
 * @author marsqing
 */
public class DefaultPullStrategy implements PullStrategy {

    private static Logger log     = LogManager.getLogger(DefaultPullStrategy.class);

    private int           failCnt = 0;
    private final int     delayBase;
    private final int     delayUpperbound;

    public DefaultPullStrategy(int delayBase, int delayUpperbound) {
        this.delayBase = delayBase;
        this.delayUpperbound = delayUpperbound;
    }

    @Override
    public long fail(boolean shouldSleep) {
        failCnt++;
        long sleepTime = (long) failCnt * delayBase;
        sleepTime = sleepTime > delayUpperbound ? delayUpperbound : sleepTime;
        if (shouldSleep) {
            if (log.isDebugEnabled()) {
                log.debug("sleep " + sleepTime + " at " + this.getClass().getSimpleName());
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return sleepTime;
    }

    @Override
    public void succeess() {
        failCnt = 0;
    }

}
