package com.api.grp.zkconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ZooServiceRegister {

    @Value("${spring.application.name}")
    private String applicationName;


}
