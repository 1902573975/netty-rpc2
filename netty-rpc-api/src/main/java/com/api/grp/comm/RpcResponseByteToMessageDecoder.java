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
        if(readL < HEAD_LENGTH){
            internalBuffer().resetReaderIndex();
            return;
        }
        byteBuf.markReaderIndex();
        int len = byteBuf.readInt();
        if(len < 0){
            channelHandlerContext.close();
            return;
        }
        if(readL < HEAD_LENGTH){
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] b = new byte[len];
        byteBuf.readBytes(b);

        ObjectMapper objectMapper = new ObjectMapper();
        RpcResponse response = objectMapper.readValue(b, RpcResponse.class);
        list.add(response);
    }
}
