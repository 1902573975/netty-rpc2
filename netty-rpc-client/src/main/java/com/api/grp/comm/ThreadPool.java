package com.api.grp.comm;

import java.util.concurrent.ConcurrentHashMap;

public class ThreadPool {

    private static final ConcurrentHashMap<String ,Thread> ts =new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String,Object> os =new ConcurrentHashMap<>();

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
    public static Object getObject(String id){
        return os.get(id);
    }
    public static void setObject(String id,Object o){
        os.put(id,o);
    }
}
