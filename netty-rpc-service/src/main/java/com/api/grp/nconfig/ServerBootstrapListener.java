package com.api.grp.nconfig;

import com.api.grp.comm.RpcRequestByteToMessageDecoder;
import com.api.grp.comm.RpcResponseMessageToByteEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Order(10000)
@Component
@ConditionalOnBean(NettyConfiguration.class)
public class ServerBootstrapListener implements ApplicationListener<ContextRefreshedEvent>{



    @Autowired
    private NettyProperties properties;
    @Autowired
    private MethodMetaMaps methodMetaMaps;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {//ApplicationReadyEvent
            new Thread(){
                @Override
                public void run() {
                    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
                    EventLoopGroup workerGroup =new NioEventLoopGroup();
                    try{
                        ServerBootstrap bootstrap =new ServerBootstrap();
                        bootstrap.group(bossGroup,workerGroup);
                        bootstrap.channel(NioServerSocketChannel.class);

//                        bootstrap.option(ChannelOption.SO_BACKLOG,128);
                        bootstrap.handler(new LoggingHandler(LogLevel.DEBUG));

                        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline pipeline = socketChannel.pipeline();
                                pipeline.addLast(new RpcRequestByteToMessageDecoder());
                                pipeline.addLast(new RpcResponseMessageToByteEncoder());
                                pipeline.addLast(new ServerChannelInBoundHandler(methodMetaMaps));

                            }
                        });
                        ChannelFuture future = bootstrap.bind(properties.getPort()).sync();
                        future.channel().closeFuture().sync();//sync这里阻塞
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }finally {
                        workerGroup.shutdownGracefully();
                        bossGroup.shutdownGracefully();
                    }

                }
            }.start();
    }
}
