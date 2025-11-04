package com.pivot.aham.common.core.support.cache;

import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.*;

/**
 * Redis连接配置
 *
 * @author addison
 * @since 2017年8月23日 上午9:36:53
 */
@Slf4j
public class RedissonClientImpl {
	/**
	 * Redis server address
	 *
	 */
	private String address;

	/**
	 * Password for Redis authentication. Should be null if not needed
	 */
	private String password;

	/**
	 * Redis cluster node urls list
	 */
	private Set<String> nodeAddresses = new HashSet<String>();

	/**
	 * Redis master server address
	 */
	private String masterAddress;

	private Integer timeout = 120000;

	/**
	 * Redis slave servers addresses
	 */
	private Set<String> slaveAddresses = new HashSet<String>();


	/**
	 * Redis sentinel servers addresses
	 */
	private String sentinelMasterName;
	private Set<String> sentinelAddresses = new HashSet<String>();

	private boolean ifReplicated;

	public org.redisson.api.RedissonClient getRedissonClient() {
		Config config = new Config();
		if (StringUtils.isNotBlank(address) && !ifReplicated) {
			SingleServerConfig serverConfig = config.useSingleServer().setAddress(address);
			if (StringUtils.isNotBlank(password)) {
				serverConfig.setPassword(password);
			}
			serverConfig.setTimeout(timeout);
		} else if (!nodeAddresses.isEmpty() && !ifReplicated) {
			ClusterServersConfig serverConfig = config.useClusterServers()
			.addNodeAddress(nodeAddresses.toArray(new String[] {}));
			if (StringUtils.isNotBlank(password)) {
				serverConfig.setPassword(password);
			}
			serverConfig.setTimeout(timeout);

		} else if (masterAddress != null && !slaveAddresses.isEmpty() && !ifReplicated) {
			MasterSlaveServersConfig serverConfig = config.useMasterSlaveServers().setMasterAddress(masterAddress)
			.addSlaveAddress(slaveAddresses.toArray(new String[] {}));

			if (StringUtils.isNotBlank(password)) {
				serverConfig.setPassword(password);
			}

			serverConfig.setTimeout(timeout);

		}else if(sentinelMasterName != null && sentinelAddresses != null){
			log.info("按哨兵模式配置,{},{}",sentinelMasterName,sentinelAddresses);
			SentinelServersConfig sentinelServersConfig = config.useSentinelServers()
			.setMasterName(sentinelMasterName)
			.addSentinelAddress(sentinelAddresses.toArray(new String[] {}));

			if (StringUtils.isNotBlank(password)) {
				sentinelServersConfig.setPassword(password);
			}

			sentinelServersConfig.setTimeout(timeout);
		}
		else if(ifReplicated){
			ReplicatedServersConfig replicatedServersConfig = config.useReplicatedServers().addNodeAddress(nodeAddresses.toArray(new String[] {})).setSslEnableEndpointIdentification(false);
			if (StringUtils.isNotBlank(password)) {
				replicatedServersConfig.setPassword(password);
			}
			replicatedServersConfig.setTimeout(timeout);

		}
		return Redisson.create(config);
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setNodeAddresses(String nodeAddresse) {
		if (nodeAddresse != null) {
			String[] nodeAddresses = nodeAddresse.split(",");
			for (int i = 0; i < nodeAddresses.length; i++) {
				if (StringUtils.isNotEmpty(nodeAddresses[i])) {
					this.nodeAddresses.add(nodeAddresses[i]);
				}
			}
		}
	}

	public void setMasterAddress(String masterAddress) {
		this.masterAddress = masterAddress;
	}

	public void setSentinelMasterName(String sentinelMasterName) {
		this.sentinelMasterName = sentinelMasterName;
	}

	public void setSlaveAddresses(String slaveAddresse) {
		if (slaveAddresse != null) {
			String[] slaveAddresses = slaveAddresse.split(",");
			for (int i = 0; i < slaveAddresses.length; i++) {
				if (StringUtils.isNotEmpty(slaveAddresses[i])) {
					this.slaveAddresses.add(slaveAddresses[i]);
				}
			}
		}
	}

	public void setSentinelAddresses(String sentinelAddress) {
		if (sentinelAddress != null) {
			String[] sentinelAddresses = sentinelAddress.split(",");
			for (int i = 0; i < sentinelAddresses.length; i++) {
				if (StringUtils.isNotEmpty(sentinelAddresses[i])) {
					this.sentinelAddresses.add(sentinelAddresses[i]);
				}
			}
		}
	}

	public boolean isIfReplicated() {
		return ifReplicated;
	}

	public void setIfReplicated(boolean ifReplicated) {
		this.ifReplicated = ifReplicated;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
}
