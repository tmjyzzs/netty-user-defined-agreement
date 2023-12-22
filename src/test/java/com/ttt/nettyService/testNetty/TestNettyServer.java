package com.ttt.nettyService.testNetty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description 测试服务器
 * DATA 2023-12-22
 *
 * @Author ttt
 */
public class TestNettyServer {

    private static final Logger logger = LoggerFactory.getLogger(TestNettyServer.class);
    @Test
    public void startServer() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.group(new NioEventLoopGroup());
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LoggingHandler());
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        logger.info("接送的数据",msg);
                    }
                });

            }

        });

        Channel channel = serverBootstrap.bind(8080).sync().channel();
        channel.closeFuture().sync();
    }
}
