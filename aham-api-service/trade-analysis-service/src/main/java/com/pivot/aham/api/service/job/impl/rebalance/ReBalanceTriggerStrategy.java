package com.pivot.aham.api.service.job.impl.rebalance;

/**
 * 触发策略
 *
 * @author addison
 * @since 2019年03月22日
 */
public interface ReBalanceTriggerStrategy {

    ReBalanceTriggerResult checkRebalance(ReBalanceTriggerContext triggerContext);

}
