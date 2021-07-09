package com.api.grp.comm;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Data
public class RpcRequest implements Serializable {

    private String threadId;

    private HashMap requestHeader;

    private Class<?> clazz;

    private String methodName;
    //请求参数
    private Object[] parameters;

    private Class<?>[] parameterTypes;

}
