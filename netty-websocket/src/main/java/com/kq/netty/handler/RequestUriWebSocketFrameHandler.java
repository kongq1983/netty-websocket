package com.kq.netty.handler;

import com.kq.netty.connection.WebSocketConnectionFacade;
import com.kq.netty.dto.UpgradeDataDto;
import com.kq.netty.util.ChannelUtil;
import com.kq.netty.util.RequestUriUtils;
import com.kq.netty.util.WebsocketResponseUtil;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;


/**
 *
 * 验证处理请求参数
 * @author kq
 * @date 2021-06-17 11:06
 * @since 2020-0630
 */
@ChannelHandler.Sharable
@Component
public class RequestUriWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    protected Logger logger = LoggerFactory.getLogger(RequestUriWebSocketFrameHandler.class);

    @Autowired
    WebSocketConnectionFacade webSocketConnectionFacade;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg!=null && msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String uri = request.uri();
            String origin = request.headers().get("Origin");
            logger.debug("origin={}",origin);
            // 不处理来源
            origin = "";
            // 没有来源，则直接关闭
            if (null == origin) {
                logger.warn("{}, 没有来源!",ctx);
                sendFailInfoCommon(ctx, request,"origin");
//                ctx.close();
                return;
            } else {
                Map<String, String> params = RequestUriUtils.getParams(uri);

                String orgId = params.get("orgId");
                String tableId = params.get("tableId");
                if(StringUtils.isEmpty(orgId)) {
                    logger.error("{}, orgId 为空!",ctx);
                    sendFailInfoCommon(ctx, request,"orgId");
                    return;
//                    sendError(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
//                    sendError(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SWITCHING_PROTOCOLS));
//                    ctx.close();
//                    return;
                }

                if(StringUtils.isEmpty(tableId)) {
                    logger.error("{}, tableId 为空!",ctx);
                    sendFailInfoCommon(ctx, request,"tableId");
                    return;
                }

                Date now = new Date();
                UpgradeDataDto dto = new UpgradeDataDto();
                dto.setOrgId(orgId);
                dto.setTableId(tableId);
                dto.setCreateTime(now);
                dto.setUpdateTime(now);


                // 校验token 合法性，这里先通过
                ChannelUtil.setUpgradeDto(ctx,dto);

                // 这里upgrade后，放客户端连接
                webSocketConnectionFacade.addConnection(ctx);

            }

            request.setUri(RequestUriUtils.getBasePath(uri));

        }
        ctx.fireChannelRead(msg);
//        super.channelRead(ctx, msg);
    }

    private void sendFailInfoCommon(ChannelHandlerContext ctx, FullHttpRequest request,String filed) {
        HttpResponseStatus failStatus = HttpResponseStatus.valueOf(10000,filed+" is valid");
//                    HttpResponseStatus failStatus = HttpResponseStatus.UNAUTHORIZED;
        // 注意 這裏的ByteBuf 用Unpooled.directBuffer()
        // 如果用byte[] 則有問題，客户端weboskcet的status不是这里指定的
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, failStatus, Unpooled.directBuffer());

        WebsocketResponseUtil.sendFailInfo(ctx,request,response);
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


//    private static void sendData(ChannelHandlerContext ctx, FullHttpRequest req) {



//        ByteBuf byteBuf = Unpooled.copiedBuffer("good", CharsetUtil.UTF_8);
//        DefaultFullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,byteBuf);
//        HttpUtil.setContentLength(res, res.content().readableBytes());
//        HttpUtil.setContentLength(res, byteBuf.array().length);

//        ctx.channel().writeAndFlush(byteBuf);
        // 沒upgradeq前，用DefaultFullHttpResponse
        // websocket 建立連接后 使用TextWebSocketFrame
//        ctx.channel().writeAndFlush(new TextWebSocketFrame("good"));


//    }


//    private static final String WSURI = Constants.WEBSOCKET_PATH;

//    /**
//     * 不建立连接
//     * @param ctx
//     * @param req
//     */
//    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // 如果HTTP解码失败，返回HHTP异常
//        if (req instanceof HttpRequest) {
//            HttpMethod method = req.method();
//            // 如果是websocket请求就握手升级
//            if (method.equals(HttpMethod.GET) && WSURI.equalsIgnoreCase(req.uri())) {
//                logger.debug("req instanceof HttpRequest");
//                WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:9090", null,
//                        false);
//                handshaker = wsFactory.newHandshaker(req);
//                if (handshaker == null) {
//                    WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
//                } else {
//                    handshaker.handshake(ctx.channel(), req);
//                }
//            }
//        }
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        logger.error("异常 exceptionCaught={}",cause);
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("建立连接: "+ctx);

    }

}
