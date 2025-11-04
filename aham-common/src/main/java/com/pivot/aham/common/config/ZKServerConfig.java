package com.pivot.aham.common.config;

import com.google.common.collect.Lists;
import com.pivot.aham.common.core.support.zk.IZKClient;
import com.pivot.aham.common.core.support.zk.ZKClientImpl;
import com.pivot.aham.common.core.util.PropertiesUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author addison
 * @create 2017-09-28 下午3:43
 **/
@Configuration
public class ZKServerConfig {

    private RetryPolicy retryPolicy = new ExponentialBackoffRetry( 1000, 3 );

    public String buildDigestString(){
        return String.format( "%s:%s",PropertiesUtil.getString("zk.realName"),PropertiesUtil.getString("zk.password") );

    }

    public ACLProvider getACLProvider(){

        return new ACLProvider() {

            private List<ACL> acl = Lists.newArrayList();

            @Override
            public List<ACL> getDefaultAcl() {
                try {
                    acl.add(new ACL( ZooDefs.Perms.ALL, new Id("digest", DigestAuthenticationProvider.generateDigest
                    (buildDigestString()))));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                return acl;
            }

            @Override
            public List<ACL> getAclForPath(String s) {
                return acl;
            }
        };
    }
    @Bean
    public CuratorFramework createCuratorClient() {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder().connectString(PropertiesUtil.getString("zk.cluster"))
            .retryPolicy( retryPolicy ).sessionTimeoutMs( PropertiesUtil.getInt("zk.sessionTimeOut") )
            .connectionTimeoutMs( PropertiesUtil.getInt("zk.connectionTimeOut") );

        if (!StringUtils.isEmpty( PropertiesUtil.getString("zk.nameSpace") )) {
            builder.namespace( PropertiesUtil.getString("zk.nameSpace") );
        }
        if (!StringUtils.isEmpty( PropertiesUtil.getString("zk.realName") ) && !StringUtils.isEmpty( PropertiesUtil.getString("zk.password") )) {
            builder.authorization( "digest", buildDigestString().getBytes() );
            builder.aclProvider( getACLProvider() );
        }

        CuratorFramework client = builder.build();
        client.start();
        try {
            client.blockUntilConnected();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return client;
    }

    @Bean
    @ConditionalOnBean(CuratorFramework.class)
    public IZKClient createZKClient(){
        return new ZKClientImpl();
    }



}
