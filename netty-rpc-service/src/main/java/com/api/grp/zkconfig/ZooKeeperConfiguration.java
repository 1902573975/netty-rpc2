package com.api.grp.zkconfig;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Closeable;
import java.io.IOException;

@Configuration
@ConditionalOnProperty(name = "zk.enabled",havingValue = "true")
@EnableConfigurationProperties(ZooProperties.class)
public class ZooKeeperConfiguration implements Closeable{

    private CuratorFramework curatorFramework;

    @Bean
    public CuratorFramework getClient(ZooProperties zoo){
        ExponentialBackoffRetry retry =new ExponentialBackoffRetry(zoo.getBaseSleepTimeMs(),zoo.getMaxRetries(),zoo.getMaxSleepMs());
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(zoo.getUrl())
                .sessionTimeoutMs(zoo.getSessionTimeoutMs())
                .connectionTimeoutMs(zoo.getConnectTimeoutMs())
                .retryPolicy(retry)
                .build();
        curatorFramework.start();
        return curatorFramework;
    }



    @Override
    public void close() throws IOException {
        CloseableUtils.closeQuietly(curatorFramework);
    }
}
