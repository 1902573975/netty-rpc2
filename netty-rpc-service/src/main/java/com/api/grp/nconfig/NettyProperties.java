package com.api.grp.nconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "netty")
public class NettyProperties {

    private int port = 9090;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
