package com.ttt.server;

import com.ttt.message.LoginRequestMessage;
import com.ttt.message.LoginResponseMessage;
import com.ttt.protocol.MessageCodecSharable;
import com.ttt.protocol.ProtocolFrameDecoder;
import com.ttt.server.handler.*;
import com.ttt.server.service.UserServiceFactory;
import com.ttt.server.session.SessionFactory;
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
        LoginRequestMessageHandler LOGIN_HANDLER = new LoginRequestMessageHandler();
        ChatRequestMessageHandler CHAT_HANDLER = new ChatRequestMessageHandler();
        GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageHandler();
        GroupJoinRequestMessageHandler GROUP_JOIN_HANDLER = new GroupJoinRequestMessageHandler();
        GroupMembersRequestMessageHandler GROUP_MEMBERS_HANDLER = new GroupMembersRequestMessageHandler();
        GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();
        GroupQuitRequestMessageHandler GROUP_QUIT_HANDLER = new GroupQuitRequestMessageHandler();
        QuitHandler QUIT_HANDLER = new QuitHandler();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.group(boss, worker);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProtocolFrameDecoder());
//                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        logger.info("读取到的数据msg>>>{}",msg);
                        super.channelRead(ctx, msg);
                    }
                });
                // deal about login logic
                ch.pipeline().addLast(LOGIN_HANDLER);
                // chat
                ch.pipeline().addLast(CHAT_HANDLER);
                // group chat
                ch.pipeline().addLast(GROUP_CREATE_HANDLER);
                ch.pipeline().addLast(GROUP_JOIN_HANDLER);
                ch.pipeline().addLast(GROUP_MEMBERS_HANDLER);
                ch.pipeline().addLast(GROUP_QUIT_HANDLER);
                ch.pipeline().addLast(GROUP_CHAT_HANDLER);
                ch.pipeline().addLast(QUIT_HANDLER);
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
