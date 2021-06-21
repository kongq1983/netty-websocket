package com.kq.netty.websocket;


import com.kq.netty.connection.WebSocketConnectionFacade;
import com.kq.netty.connection.WebSocketConnectionOrgHolder;
import com.kq.netty.connection.WebSocketConnectionTableHolder;
import com.kq.netty.util.ChannelUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author kq
 * @date 2021-04-21 17:57
 * @since 2020-0630
 */
@ChannelHandler.Sharable
@Component
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger logger = LoggerFactory.getLogger(ChatHandler.class);

    //存储用户连接
    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 餐厅连接列表
     */
    @Autowired
    WebSocketConnectionFacade webSocketConnectionFacade;


    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:MM:ss");

    // 当Channel中有新的事件消息会自动调用
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String text=textWebSocketFrame.text();
        System.out.println("接收到消息："+text);
        System.out.println("接收到UgradeData："+ ChannelUtil.getUpgradeDto(ctx));

        // 获取客户端发送过来的文本消息
        for (Channel client :clients) {
            // 将消息发送到所有的客户端
            client.writeAndFlush(new TextWebSocketFrame(sdf.format(new Date())+":"+text));
        }
    }

    // 当有新的客户端连接服务器之后，会自动调用这个方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("建立连接: "+ctx);
        clients.add(ctx.channel());


    }

    // 端口连接处理
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        logger.info("断开连接: "+ctx);
        clients.remove(ctx.channel());

        webSocketConnectionFacade.deleteConnection(ctx);

    }
}
