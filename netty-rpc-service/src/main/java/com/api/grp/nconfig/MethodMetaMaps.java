package com.api.grp.nconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class MethodMetaMaps{

    private ConcurrentHashMap<String,RpcMetadata> hashMap = new ConcurrentHashMap();

    //实例化后的接口集合
    private HashSet<Class> instanceIntSet = null;



    public void addMetadata(String id, RpcMetadata metadata){
        hashMap.put(id,metadata);

    }

    public RpcMetadata getMetadata(String id) {
        return hashMap.get(id);
    }

    public HashSet<Class> getInstanceIntSet() {
        return instanceIntSet;
    }

    public void setInstanceIntSet(HashSet<Class> instanceIntSet) {
        this.instanceIntSet = instanceIntSet;
    }
}

