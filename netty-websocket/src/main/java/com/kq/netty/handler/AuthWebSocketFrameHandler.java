package com.kq.netty.handler;

import com.kq.netty.util.RequestUriUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import jdk.nashorn.internal.objects.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 处理请求参数
 * @author kq
 * @date 2021-06-17 11:06
 * @since 2020-0630
 */
public class AuthWebSocketFrameHandler extends SimpleChannelInboundHandler<Object> {

    protected Logger logger = LoggerFactory.getLogger(AuthWebSocketFrameHandler.class);

    WebSocketServerHandshaker handshaker;

    int port;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof FullHttpRequest) {
            // 处理http消息（升级协议）
            handlerHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            // 处理websocket消息
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
        }

    }

    private void handlerHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // 如果不是升级协议消息
        if (!req.getDecoderResult().isSuccess() || !"websocket".equals(req.headers().get("Upgrade"))) {
            sendError(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        // 设置连接参数
        ctx.attr(AttributeKey.valueOf("channelId")).set(ctx.channel().id());
        System.out.println(ctx.channel().remoteAddress().toString() + "----" + ctx.channel().id());

        //websocket协议开头为：ws+ip+端口
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:" + this.port, null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (wsFactory == null) {
            //返回不支持websocket 版本
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            //开始握手
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 判断是否为关闭链路指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame);
            return;
        }
        // ping请求返回pong
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 仅支持文本信息
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame not support", frame.getClass().getName()));
        }
        String text = ((TextWebSocketFrame) frame).text();
        System.out.println(String.format("Client:%s,channelId:%s", text, ctx.attr(AttributeKey.valueOf("channelId")).get()));

        TextWebSocketFrame tws = new TextWebSocketFrame(String.format("服务器收到消息:%s,通道id:%s,当前时间:%s", text, ctx.channel().id(), LocalDateTime.now()));
        if (text.startsWith("$")) {
            //已$开头的单独回复
            ctx.channel().write(tws);
        } else {
            //群发
//            Global.group.writeAndFlush(tws);
        }
    }

    private static void sendError(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
        if (res.getStatus().code() != 200) {
            ByteBuf byteBuf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(byteBuf);
            byteBuf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }
        ChannelFuture channelFuture = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.getStatus().code() != 200) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }



}
