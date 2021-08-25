package com.api.grp.comm;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcResponseByteToMessageDecoder extends ByteToMessageDecoder {

    public static final int HEAD_LENGTH = RpcRequestMessageToByteEncoder.HEAD_LENGTH;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int readL =byteBuf.readableBytes();
//        System.out.println("readL:"+readL);
        if(readL <= HEAD_LENGTH){
            byteBuf.resetReaderIndex();
            System.out.println("responseDecode:resetReaderIndex");
            return;
        }

        int len = byteBuf.readInt();
        if(len <= 0){
            channelHandlerContext.close();
            System.out.println("responseDecode:close");
            return;
        }

        byte[] b = new byte[len];
        byteBuf.readBytes(b);

        ObjectMapper objectMapper = new ObjectMapper();
        RpcResponse response = objectMapper.readValue(b, RpcResponse.class);
        list.add(response);
    }
}
