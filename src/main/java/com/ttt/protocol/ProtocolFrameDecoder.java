package com.ttt.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Description 重写一个帧解码器   方便使用
 * DATA 2023-12-22
 *
 * @Author ttt
 */
public class ProtocolFrameDecoder  extends LengthFieldBasedFrameDecoder {
    public ProtocolFrameDecoder() {
        this(1024, 12, 4, 0, 0);
    }

    public ProtocolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }


}
