package com.api.grp.comm;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcResponseMessageToByteEncoder extends MessageToByteEncoder<RpcResponse> {

    public static final int HEAD_LENGTH = RpcRequestMessageToByteEncoder.HEAD_LENGTH;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse, ByteBuf byteBuf) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] b = objectMapper.writeValueAsBytes(rpcResponse);
        int length = b.length;
        byteBuf.writeInt(length);
        byteBuf.writeBytes(b);
        byteBuf.writeBytes(CommV.delimiter.getBytes());
    }
}
