package com.api.grp.nconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "netty.enabled",havingValue = "true")
@EnableConfigurationProperties(NettyProperties.class)
public class NettyConfiguration {


    @Bean
    public MethodMetaMaps getMetaCollection(){
        return new MethodMetaMaps();
    }

}
