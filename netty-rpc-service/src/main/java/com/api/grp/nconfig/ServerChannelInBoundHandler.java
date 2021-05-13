package com.api.grp.nconfig;

import com.alibaba.fastjson.JSONObject;
import com.api.grp.comm.RpcRequest;
import com.api.grp.comm.RpcResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;


public class ServerChannelInBoundHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private MethodMetaMaps methodMetaMaps;

    public ServerChannelInBoundHandler(MethodMetaMaps methodMetaMaps){
        this.methodMetaMaps = methodMetaMaps;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        RpcResponse response =new RpcResponse();
        String threadId = rpcRequest.getThreadId();
        String methodSignId = rpcRequest.getMethodSignId();
        response.setThreadId(threadId);

        try{
            RpcMetadata metadata = methodMetaMaps.getMetadata(methodSignId);
            if(metadata == null){
                response.setCode("404");
                response.setMsg("接口不存在");
                return;
            }


            Method m = metadata.getM();
            Object targetObject = metadata.getTargetObject();
            Object o;
            Class[] parameterTypes = metadata.getParameterTypes();
            List<Object> requestBody = rpcRequest.getRequestBody();
            Object[] param =new Object[parameterTypes.length];
            Object p;
            ObjectMapper objectMapper = new ObjectMapper();

            if(parameterTypes.length != 0){
                for(int i=0;i<parameterTypes.length;i++){
                    o = requestBody.get(i);
                    if(o!= null && o instanceof HashMap){
                        byte[] bytes = objectMapper.writeValueAsBytes(o);
                        p = objectMapper.readValue(bytes,parameterTypes[i]);
                        param[i] = p;
                    }else{
                        param[i] = requestBody.get(i);
                    }
                }
            }

            //调用方法
            Object result =null;
            try{
                if(parameterTypes.length == 0){
                    result = m.invoke(targetObject);
                }else{
                    result = m.invoke(targetObject,param);
                }
            }catch (Exception e){
                response.setCode("500");
                response.setMsg("内部服务错误");
                throw e;
            }
            response.setData(result);
            response.setCode("200");
            response.setMsg("正常");
        }catch (Exception e){
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
