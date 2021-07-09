package com.api.grp.comm;

import java.util.concurrent.ConcurrentHashMap;

public class ThreadPool {

    private static final ConcurrentHashMap<String ,Thread> ts =new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String,RpcResponse> os =new ConcurrentHashMap<>();

    public static void addThread(String id,Thread t){
        ts.put(id,t);
    }

    public static Thread getThread(String id){
        return ts.get(id);
    }

    public static void notify(String id) {
        Thread thread = getThread(id);
        if(thread != null){
            thread.setPriority(9);
            thread.interrupt();
        }
    }
    public static RpcResponse getObject(String id){
        return os.get(id);
    }
    public static void setObject(String id,RpcResponse o){
        os.put(id,o);
    }
}
