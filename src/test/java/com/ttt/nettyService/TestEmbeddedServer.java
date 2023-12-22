package com.ttt.nettyService;

import com.ttt.message.LoginRequestMessage;
import com.ttt.protocol.MessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Description TODO
 * DATA 2023-12-21
 *
 * @Author ttt
 */
public class TestEmbeddedServer {

    private static final Logger logger = LoggerFactory.getLogger(TestEmbeddedServer.class);

    @Test
    public void firstSendMsg() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LoggingHandler(),
//                new LengthFieldBasedFrameDecoder(
//                        1024, 12, 4, 0, 0),
                new MessageCodec());
        // encode
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "张三");
//        channel.writeOutbound(message);
        // decode
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buf);
        channel.writeInbound(buf);
        ByteBuf buf1 = (ByteBuf) channel.readOutbound();
        ArrayList<Object> objects = new ArrayList<>();
//        new MessageCodec().decode(null,buf,objects);


    }

}
