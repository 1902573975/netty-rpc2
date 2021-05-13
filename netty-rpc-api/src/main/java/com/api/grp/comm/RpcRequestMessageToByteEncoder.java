package com.api.grp.comm;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcRequestMessageToByteEncoder extends MessageToByteEncoder<RpcRequest> {


    public static final int HEAD_LENGTH = 4;// 4 * 8 =32

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest, ByteBuf byteBuf) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] b = objectMapper.writeValueAsBytes(rpcRequest);
        int length = b.length;
        byteBuf.writeInt(length);
        byteBuf.writeBytes(b);
    }
}
