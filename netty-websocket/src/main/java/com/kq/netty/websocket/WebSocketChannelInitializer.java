package com.kq.netty.websocket;

import com.kq.netty.connection.WebSocketConnectionFacade;
import com.kq.netty.constants.Constants;
import com.kq.netty.handler.RequestUriWebSocketFrameHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *  通道初始化器
 *  * 用来加载通道处理器（ChannelHandler)
 *
 * @author kq
 * @date 2021-04-21 17:56
 * @since 2020-0630
 */
@Component
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private WebSocketConnectionFacade webSocketConnectionFacade;

    @Autowired
    private RequestUriWebSocketFrameHandler requestUriWebSocketFrameHandler;


    @Autowired
    private ChatHandler chatHandler;

    // 初始化通道
    //
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        // 获取管道，将一个一个的ChannelHandler添加到管道中
        ChannelPipeline pipeline = socketChannel.pipeline();

        // 添加一个http的编解码器
        pipeline.addLast(new HttpServerCodec());
        // 添加一个用于支持大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());
        // 添加一个聚合器，这个聚合器主要是将HttpMessage聚合成FullHttpRequest/Response
        pipeline.addLast(new HttpObjectAggregator(1024*64));
        // 处理请求参数
        pipeline.addLast(requestUriWebSocketFrameHandler);
//        pipeline.addLast(new RequestUriWebSocketFrameHandler());
        // 需要指定接收请求的路由
        // 必须使用以ws后缀结尾的url才能访问
        pipeline.addLast(new WebSocketServerProtocolHandler(Constants.WEBSOCKET_PATH));
//        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        // 添加自定义的Handler
        pipeline.addLast(chatHandler);
//        pipeline.addLast(new ChatHandler());

        // 增加心跳事件支持
        // 第一个参数:  读空闲4秒
        // 第二个参数： 写空闲8秒
        // 第三个参数： 读写空闲12秒
        pipeline.addLast(new IdleStateHandler(4,8,12));
        pipeline.addLast(new HearBeatHandler());
    }
}
