package com.api.grp.comm;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class RpcRequest implements Serializable {

    private String threadId;

    private String methodSignId;

    private HashMap<String,String> requestHeader;

    private List<Object> requestBody;

    public String getMethodSignId() {
        return methodSignId;
    }

    public void setMethodSignId(String methodSignId) {
        this.methodSignId = methodSignId;
    }

    public HashMap<String, String> getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(HashMap<String, String> requestHeader) {
        this.requestHeader = requestHeader;
    }

    public List<Object> getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(List<Object> requestBody) {
        this.requestBody = requestBody;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }
}
