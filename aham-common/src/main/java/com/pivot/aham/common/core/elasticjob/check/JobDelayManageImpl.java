package com.pivot.aham.common.core.elasticjob.check;

import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.internal.config.LiteJobConfigurationGsonFactory;
import com.dangdang.ddframe.job.lite.internal.storage.JobNodePath;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.slf4j.Logger;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class JobDelayManageImpl implements JobDelayManage {

    /**
     * 用于快速定位JobDelayMessage
     */
    private final Map<String, JobDelayMessage> delayMapInner = Maps.newHashMap();
    private final Set<JobDelayMessage> jobDelayMessageList = Sets.newHashSet();

    public JobDelayManageImpl(CoordinatorRegistryCenter regCenter, MessageSend messageSend) {
        regCenterInner = regCenter;
        messageSendInner = messageSend;
    }

    private final MessageSend messageSendInner;
    private final CoordinatorRegistryCenter regCenterInner;

    @Override
    public Map<String, JobDelayMessage> getDelayMapInner() {
        return delayMapInner;
    }

    @Override
    public Set<JobDelayMessage> getJobDelayMessageList() {
        return jobDelayMessageList;
    }

    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    public MessageSend getMessageSend() {
        return messageSendInner;
    }

    @Override
    public Set<String> listjobName() {
        return Sets.newHashSet(regCenterInner.getChildrenKeys("/"));
    }

    @Override
    public boolean registerWatcherNodeChangedForever(final String jobName) {
        Object rawClient = regCenterInner.getRawClient();

        if (!(rawClient instanceof CuratorFramework)) {
            log.error("rawClient instanceof CuratorFramework is false.");
            return false;
        }
        String jobShardingNodePath = new JobNodePath(jobName).getShardingNodePath();
        //TODO 可以考虑zk动态获取自己的节点数据 来达到分片任务由各自监控
        String shardingNodePathFirstChildPath = jobShardingNodePath + "/0";
        //elasticjob 第一次trigger的时候才会产生sharding节点
        if (!regCenterInner.isExisted(shardingNodePathFirstChildPath)) {
            regCenterInner.persistEphemeral(shardingNodePathFirstChildPath, "");
        }
        PathChildrenCache childrenCache = new PathChildrenCache((CuratorFramework) rawClient, shardingNodePathFirstChildPath, true);
        final String runningNode = "running";
        try {
            childrenCache.getListenable().addListener((curatorFramework, pathChildrenCacheEvent) -> {
                List<String> childrenKeys = regCenterInner.getChildrenKeys(shardingNodePathFirstChildPath);
                if (pathChildrenCacheEvent.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
                    if (childrenKeys.contains(runningNode)) {
                        updateJobDelayMessageStatusToTrue(jobName);
                    }
                }
            });
            childrenCache.start();
        } catch (Exception e) {
            log.error("registerWatcherNodeChanged error:{}.", e);
            return false;
        }

        return true;
    }

    @Override
    public JobDelayMessage getJobDelayMessage(final String jobName) {
        JobNodePath jobNodePath = new JobNodePath(jobName);

        String liteJobConfigJson = regCenterInner.get(jobNodePath.getConfigNodePath());
        if (null == liteJobConfigJson) {
            return null;
        }
        LiteJobConfiguration liteJobConfig = LiteJobConfigurationGsonFactory.fromJson(liteJobConfigJson);
        String cron = liteJobConfig.getTypeConfig().getCoreConfig().getCron();
        Date nextValidTime = null;
        try {
            nextValidTime = JobDelayTool.getNextValidTime(new Date(), cron);
        } catch (ParseException e) {
        }
        JobDelayMessage result = new JobDelayMessage(jobName, cron, nextValidTime.getTime());
        return result;
    }

}
