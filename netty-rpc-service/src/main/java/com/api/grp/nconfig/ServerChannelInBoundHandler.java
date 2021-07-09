package com.api.grp.nconfig;

import com.alibaba.fastjson.JSONObject;
import com.api.grp.comm.RpcRequest;
import com.api.grp.comm.RpcResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class ServerChannelInBoundHandler extends SimpleChannelInboundHandler<RpcRequest> {

    ApplicationContext applicationContext;

    public ServerChannelInBoundHandler(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        RpcResponse response =new RpcResponse();
        String threadId = rpcRequest.getThreadId();
        Class<?> clazz = rpcRequest.getClazz();
        String methodName = rpcRequest.getMethodName();
        Object[] parameters = rpcRequest.getParameters();
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        response.setThreadId(threadId);
        //对参数进行初始化
        if(parameterTypes !=null && parameterTypes.length > 0){
            ObjectMapper objectMapper = new ObjectMapper();
            for(int i =0;i< parameterTypes.length;i++){
                if(parameters[i] instanceof LinkedHashMap && !HashMap.class.isAssignableFrom(parameterTypes[i])){
                    byte[] bytes = objectMapper.writeValueAsBytes(parameters[i]);
                    parameters[i] = objectMapper.readValue(bytes,parameterTypes[i]);
                }
            }
        }
        Object bean =null;
        Object result=null;
        try{
            Method method = clazz.getMethod(methodName, parameterTypes);
            bean = applicationContext.getBean(clazz);
            try{
                result = method.invoke(bean,parameters);
                response.setData(result);
                response.setCode("200");
                response.setMsg("正常");
            }catch (Throwable throwable){
                response.setCode("500");
                response.setMsg("内部服务错误");
                throwable.printStackTrace();

            }
        }catch (Throwable e){
            response.setCode("500");
            response.setMsg("调用服务错误");
            e.printStackTrace();
        }
        channelHandlerContext.channel().writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println("error'");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }
}
