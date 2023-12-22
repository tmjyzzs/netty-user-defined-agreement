package com.ttt.server;

import com.ttt.message.LoginRequestMessage;
import com.ttt.message.LoginResponseMessage;
import com.ttt.protocol.MessageCodecSharable;
import com.ttt.protocol.ProtocolFrameDecoder;
import com.ttt.server.service.UserServiceFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description 聊天服务器
 * DATA 2023-12-22
 *
 * @Author ttt
 */
public class ChatServer {

    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.group(boss, worker);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
//                ch.pipeline().addLast(new ProtocolFrameDecoder());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        ByteBuf buf = (ByteBuf) msg;
                        logger.info("读取到的数据>>>{}",buf);
                        super.channelRead(ctx, msg);
                    }
                });
                ch.pipeline().addLast(new SimpleChannelInboundHandler<LoginRequestMessage>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
                        logger.info("接收到的消息",msg);
                        String username = msg.getUsername();
                        String password = msg.getPassword();
                        boolean login = UserServiceFactory.getUserService().login(username, password);

                        LoginResponseMessage message;
                        if(login) {
                            message = new LoginResponseMessage(true, "登录成功");
                        } else {
                            message = new LoginResponseMessage(false, "用户名或密码不正确");
                        }
                        ctx.writeAndFlush(message);
                    }
                });
            }
        });
        Channel channel = null;
        try {
            channel = serverBootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }
}
