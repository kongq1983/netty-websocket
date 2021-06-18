package com.kq.netty.handler;

import com.kq.netty.util.RequestUriUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;


/**
 * 处理请求参数
 * @author kq
 * @date 2021-06-17 11:06
 * @since 2020-0630
 */
public class RequestUriWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    protected Logger logger = LoggerFactory.getLogger(RequestUriWebSocketFrameHandler.class);

    WebSocketServerHandshaker handshaker;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg!=null && msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String uri = request.uri();
            String origin = request.headers().get("Origin");
            logger.debug("orgiin={}",origin);
            // 没有来源，则直接关闭
            if (null == origin) {
                logger.warn("{}, 没有来源!",ctx);
                ctx.close();
                return;
            } else {
                Map<String, String> params = RequestUriUtils.getParams(uri);
                String token = params.get("token");
                if(StringUtils.isEmpty(token)) {
                    logger.error("{}, 没有Token!",ctx);
//                    ctx.channel().writeAndFlush("认证失败！");

//                    byte[] bytes = "认证失败！".getBytes();
//
//                    HttpResponseStatus httpResponseStatus = HttpResponseStatus.UNAUTHORIZED;
//
//                    FullHttpResponse response = new DefaultFullHttpResponse(
//                            HTTP_1_1, httpResponseStatus, Unpooled.wrappedBuffer(bytes));
////                    ctx.channel().writeAndFlush(response);
//                    ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);


//                    ByteBuf buf = Unpooled.buffer(bytes.length);
//                    buf.writeBytes(bytes);
//                    buf.clear();

//                    ctx.channel().writeAndFlush(buf);
                    // 先升級
                    this.handleHttpRequest(ctx,request);

                    sendData(ctx, request);

//                    sendError(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
//                    sendError(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SWITCHING_PROTOCOLS));

//                    ctx.close();
//                    return;
                }

                // 校验token 合法性，这里先通过
            }

            request.setUri(RequestUriUtils.getBasePath(uri));

        }
        ctx.fireChannelRead(msg);
//        super.channelRead(ctx, msg);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

    }

//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg,DefaultFullHttpResponse res) throws Exception {
//        ByteBuf byteBuf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
//        res.content().writeBytes(byteBuf);
//        byteBuf.release();
//        HttpUtil.setContentLength(res, res.content().readableBytes());
//        ChannelFuture channelFuture = ctx.channel().writeAndFlush(res);
//    }


    private static void sendData(ChannelHandlerContext ctx, FullHttpRequest req) {



//        ByteBuf byteBuf = Unpooled.copiedBuffer("good", CharsetUtil.UTF_8);
//        DefaultFullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,byteBuf);
//        HttpUtil.setContentLength(res, res.content().readableBytes());
//        HttpUtil.setContentLength(res, byteBuf.array().length);

//        ctx.channel().writeAndFlush(byteBuf);
        // 沒upgradeq前，用DefaultFullHttpResponse
        // websocket 建立連接后 使用TextWebSocketFrame
        ctx.channel().writeAndFlush(new TextWebSocketFrame("good"));


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

    private static final String WSURI = "/ws";

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // 如果HTTP解码失败，返回HHTP异常
        if (req instanceof HttpRequest) {
            HttpMethod method = req.method();
            // 如果是websocket请求就握手升级
            if (method.equals(HttpMethod.GET) && WSURI.equalsIgnoreCase(req.uri())) {
                logger.debug("req instanceof HttpRequest");
                WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:9090", null,
                        false);
                handshaker = wsFactory.newHandshaker(req);
                if (handshaker == null) {
                    WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                } else {
                    handshaker.handshake(ctx.channel(), req);
                }
            }
        }
    }


}
