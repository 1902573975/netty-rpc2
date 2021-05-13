package com.api.grp.comm;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class NettyClient implements ApplicationListener<ContextRefreshedEvent> {//ApplicationReadyEvent
    @Value("${netty.ip}")
    private String ip;
    @Value("${netty.port}")
    private Integer port;

    private ChannelFuture sync;

    private Channel channel;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        new Thread(){
            @Override
            public void run() {
                EventLoopGroup bossGroup = new NioEventLoopGroup();
                try{
                    Bootstrap bootstrap =new Bootstrap();
                    bootstrap.group(bossGroup);
                    bootstrap.channel(NioSocketChannel.class);
                    bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                            pipeline.addLast(new RpcRequestMessageToByteEncoder());

                            pipeline.addLast(new RpcResponseByteToMessageDecoder());
                            pipeline.addLast(new ClientChannelInboundHandlerAdapter());//.addLast(outboundHandlerAdapter);

                        }
                    });
                    sync = bootstrap.connect(ip, port);
                    channel = sync.channel();
                    sync.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if(future.isSuccess()){
//                                System.out.println("bsucc");
                            }else{
                                System.out.println("连接失败");
                            }
                        }
                    });

                }catch (Exception e){
                    throw new RuntimeException(e);
                }finally {
                }
            }
        }.start();
    }

    public Channel getChannel() {
        return channel;
    }


}
