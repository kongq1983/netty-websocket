package com.kq.netty.websocket;


import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author kq
 * @date 2021-04-21 17:57
 * @since 2020-0630
 */


public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //存储用户连接
    private static ChannelGroup clients=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-mm-dd hh:MM:ss");

    // 当Channel中有新的事件消息会自动调用
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String text=textWebSocketFrame.text();
        System.out.println("接收到消息："+text);

        // 获取客户端发送过来的文本消息
        for (Channel client :clients) {
            // 将消息发送到所有的客户端
            client.writeAndFlush(new TextWebSocketFrame(sdf.format(new Date())+":"+text));
        }
    }

    // 当有新的客户端连接服务器之后，会自动调用这个方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("建立连接:"+ctx);
        clients.add(ctx.channel());
    }

    // 端口连接处理
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("断开连接"+ctx);
        clients.remove(ctx.channel());
    }
}
