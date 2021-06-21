package com.kq.netty.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.CharsetUtil;

/**
 * @author kq
 * @date 2021-06-21 9:00
 * @since 2020-0630
 */
public interface WebsocketResponseUtil {

    /**
     * 发送失败信息
     * @param ctx
     * @param req
     * @param res
     */
    public static void sendFailInfo(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        if (res.status().code() != 200) {
            ByteBuf byteBuf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(byteBuf);
            byteBuf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }
        ChannelFuture channelFuture = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

}
