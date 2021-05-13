package com.api.grp.nconfig;

import java.io.Serializable;
import java.lang.reflect.Method;

public class RpcMetadata implements Serializable {

    private Object targetObject; //实现类

    private String intClazzName; //接口类全名

    private Method m;

    private String method;

    private Class<?>[] parameterTypes;

    private Class<?> returnType;

    public RpcMetadata() {
    }

    public RpcMetadata(Object targetObject, String intClazzName, Method m ,String method, Class<?>[] parameterTypes, Class<?> returnType) {
        this.targetObject = targetObject;
        this.intClazzName = intClazzName;
        this.method = method;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
        this.m = m;
    }

    public Object getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(Object targetObject) {
        this.targetObject = targetObject;
    }

    public String getIntClazzName() {
        return intClazzName;
    }

    public void setIntClazzName(String intClazzName) {
        this.intClazzName = intClazzName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public Method getM() {
        return m;
    }

    public void setM(Method m) {
        this.m = m;
    }
}
