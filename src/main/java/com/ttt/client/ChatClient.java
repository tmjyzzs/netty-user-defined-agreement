package com.ttt.client;

import com.ttt.message.ChatRequestMessage;
import com.ttt.message.LoginRequestMessage;
import com.ttt.message.LoginResponseMessage;
import com.ttt.protocol.MessageCodecSharable;
import com.ttt.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Description 客户端
 * DATA 2023-12-22
 *
 * @Author ttt
 */
@Slf4j
public class ChatClient {

    public static void main(String[] args)  {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
        AtomicBoolean LOGIN = new AtomicBoolean(false);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new ProtocolFrameDecoder());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.debug("msg: {}", msg);
                        if ((msg instanceof LoginResponseMessage)) {
                            LoginResponseMessage response = (LoginResponseMessage) msg;
                            if (response.isSuccess()) {
                                // 如果登录成功
                                LOGIN.set(true);
                            }
                            // 唤醒 system in 线程
                            WAIT_FOR_LOGIN.countDown();
                        }
                    }

                    // 在连接建立后触发 active 事件
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {

                        // 负责接收用户在控制台的输入，负责向服务器发送各种消息
                        new Thread(() -> {
                            Scanner scanner = new Scanner(System.in);
                            System.out.println("请输入用户名:");
                            String username = scanner.nextLine();
                            System.out.println("请输入密码:");
                            String password = scanner.nextLine();
                            // 构造消息对象
                            LoginRequestMessage message = new LoginRequestMessage(username, password);
//                            ByteBuf buffer = ctx.alloc().buffer();
//                            buffer.writeBytes("nihao".getBytes());
                            // 发送消息
                            ctx.writeAndFlush(message);
                            System.out.println("等待后续操作...");
                            try {
                                WAIT_FOR_LOGIN.await();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            // 如果登录失败
                            if (!LOGIN.get()) {
                                System.out.println("登入失败重新");
                                ctx.channel().close();
                                return;
                            }
                            while (true) {
                                System.out.println("==================================");
                                System.out.println("send [username] [content]");
                                System.out.println("gsend [group name] [content]");
                                System.out.println("gcreate [group name] [m1,m2,m3...]");
                                System.out.println("gmembers [group name]");
                                System.out.println("gjoin [group name]");
                                System.out.println("gquit [group name]");
                                System.out.println("quit");
                                System.out.println("==================================");
                                String command = scanner.nextLine();
                                String[] s = command.split(" ");
                                switch (s[0]) {
                                    case "send":
                                        ctx.writeAndFlush(new ChatRequestMessage(username, s[1], s[2]));
                                        break;
//                                    case "gsend":
//                                        ctx.writeAndFlush(new GroupChatRequestMessage(username, s[1], s[2]));
//                                        break;
//                                    case "gcreate":
//                                        Set<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
//                                        set.add(username); // 加入自己
//                                        ctx.writeAndFlush(new GroupCreateRequestMessage(s[1], set));
//                                        break;
//                                    case "gmembers":
//                                        ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
//                                        break;
//                                    case "gjoin":
//                                        ctx.writeAndFlush(new GroupJoinRequestMessage(username, s[1]));
//                                        break;
//                                    case "gquit":
//                                        ctx.writeAndFlush(new GroupQuitRequestMessage(username, s[1]));
//                                        break;
                                    case "quit":
                                        ctx.channel().close();
                                        return;
                                }
                            }
                        }, "system in").start();
                    }
                });
            }
        });

        try {
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            group.shutdownGracefully();
        }

    }
}
