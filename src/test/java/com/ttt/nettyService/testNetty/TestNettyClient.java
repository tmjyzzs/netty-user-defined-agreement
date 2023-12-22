package com.ttt.nettyService.testNetty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Description 测试客户端
 * DATA 2023-12-22
 *
 * @Author ttt
 */
public class TestNettyClient {

    private static final Logger log = LoggerFactory.getLogger(TestNettyClient.class);

    @Test
    public void sendMsg() throws InterruptedException {

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LoggingHandler());
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        log.debug("sending...");
                        Random r = new Random();
                        char c = 'a';
                        ByteBuf buffer = ctx.alloc().buffer();
                        for (int i = 0; i < 10; i++) {
                            for (int j = 1; j <= r.nextInt(16)+1; j++) {
                                buffer.writeByte((byte) c);
                                log.info("byte字节{}",(byte) c);
                                log.info("buffer{}",buffer.getByte(1));
                            }
                            buffer.writeByte(10);
                            c++;
                        }
                        ctx.writeAndFlush(buffer);
                    }
                });
            }
        });
        ChannelFuture localHost = bootstrap.connect("127.0.0.1", 8080).sync();
        localHost.channel().closeFuture().sync();
    }
}
