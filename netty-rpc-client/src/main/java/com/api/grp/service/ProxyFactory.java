package com.api.grp.service;

import com.api.grp.api.IAccountService;
import com.api.grp.comm.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProxyFactory {

    private ConcurrentHashMap<Class,Object> hashMap =new ConcurrentHashMap<>();

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private NettyClient nettyClient;

    public Object proxy(Class clazz){

        Object o = hashMap.get(clazz);

        if(StringUtils.isEmpty(applicationName)){
            throw new RuntimeException("应用名称不能为空");
        }
        if(clazz.isInterface()){
            RPCService annotation = (RPCService)clazz.getAnnotation(RPCService.class);
            if(annotation != null && !annotation.serviceName().equals(applicationName)){
                //proxy
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(clazz);
                enhancer.setCallback(new MethodInterceptor() {
                    @Override
                    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                        Thread t = Thread.currentThread();
                        String id = Long.toString(t.getId());
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        String name = method.getName();
                        Class<?> clazz = method.getDeclaringClass();

                        RpcRequest request = new RpcRequest();
                        request.setMethodName(name);
                        request.setParameters(objects);
                        request.setParameterTypes(parameterTypes);
                        request.setThreadId(id);
                        request.setClazz(clazz);

                        nettyClient.getChannel().writeAndFlush(request).addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {
                                if(future.isSuccess()){
//                                    System.out.println("发送成功");
                                }else{
                                    System.out.println("发送失败");
                                }
                            }
                        });
                        ThreadPool.addThread(id,t);
                        try{
                            t.sleep(20000L);
                        }catch (InterruptedException ie){
                        }finally {
                            RpcResponse obj = ThreadPool.getObject(id);
                            if("200".equals(obj.getCode()) && obj.getData() != null){
                                Object data = obj.getData();
                                if(data instanceof HashMap){
                                    try{
                                        ObjectMapper objectMapper = new ObjectMapper();
                                        Class<?> returnType = method.getReturnType();
                                        byte[] bytes = objectMapper.writeValueAsBytes(data);
                                        return objectMapper.readValue(bytes,returnType);
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                }
                                return data;
                            }
                            return null;
                        }
                    }
                });
                o = enhancer.create();
                hashMap.put(clazz,o);
                return o;
            }
        }else{
            throw new RuntimeException("必须为接口");
        }
        return o;

    }

    public void test(){
        nettyClient.getChannel().writeAndFlush("Are");
    }
}
