package com.kq.netty.connection;

import com.kq.netty.dto.UpgradeDataDto;
import com.kq.netty.util.ChannelUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 连接门面
 * @author kq
 * @date 2021-06-21 16:26
 * @since 2020-0630
 */

@Component
public class WebSocketConnectionFacade {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConnectionFacade.class);

    /**
     * 餐厅连接列表
     */
    private WebSocketConnectionOrgHolder webSocketConnectionOrgHolder = new WebSocketConnectionOrgHolder();

    /**
     * 餐桌连接列表
     */
    private WebSocketConnectionTableHolder webSocketConnectionTableHolder = new WebSocketConnectionTableHolder();


    public WebSocketConnectionOrgHolder getWebSocketConnectionOrgHolder() {
        return webSocketConnectionOrgHolder;
    }

    public WebSocketConnectionTableHolder getWebSocketConnectionTableHolder() {
        return webSocketConnectionTableHolder;
    }

    public void addConnection(ChannelHandlerContext ctx){

        UpgradeDataDto dto = ChannelUtil.getUpgradeDto(ctx);
        if(dto==null){
            logger.warn("获取不到UpgradeDataDto channelId={}",ctx.channel().id().asLongText());
            return;
        }

        Channel channel = ctx.channel();

        String orgId = dto.getOrgId();
        String tableId = dto.getTableId();

        // 餐厅
        webSocketConnectionOrgHolder.addChannel(orgId,channel);
        // 桌子
        webSocketConnectionTableHolder.addChannel(tableId,channel);

    }

    public void deleteConnection(ChannelHandlerContext ctx){

        UpgradeDataDto dto = ChannelUtil.getUpgradeDto(ctx);
        if(dto==null){
            logger.warn("获取不到UpgradeDataDto channelId={}",ctx.channel().id().asLongText());
            return;
        }

        Channel channel = ctx.channel();

        String orgId = dto.getOrgId();
        String tableId = dto.getTableId();

        // 餐厅
        webSocketConnectionOrgHolder.deleteChannel(orgId,channel.id().asLongText());
        // 桌子
        webSocketConnectionTableHolder.deleteChannel(tableId,channel.id().asLongText());

    }

    public void notifyAll(ChannelHandlerContext ctx,String data, int type){

        UpgradeDataDto dto = ChannelUtil.getUpgradeDto(ctx);
        if(dto==null){
            logger.warn("获取不到UpgradeDataDto channelId={}",ctx.channel().id().asLongText());
            return;
        }

        String orgId = dto.getOrgId();
        String tableId = dto.getTableId();


        if(type==WebSocketConnectionBase.NOFITY_ALL) {
            webSocketConnectionOrgHolder.nofityByKey(orgId,data);
            webSocketConnectionTableHolder.nofityByKey(tableId,data);
        }else if(type==WebSocketConnectionBase.NOFITY_TABLE) {
            webSocketConnectionTableHolder.nofityByKey(tableId,data);
        }else if(type==WebSocketConnectionBase.NOFITY_ORG) {
            webSocketConnectionOrgHolder.nofityByKey(orgId,data);
        }

    }

    public void notifyTable(String tableId,String data){
        webSocketConnectionTableHolder.nofityByKey(tableId,data);
    }

    public void notifyOrg(String orgId,String data){
        webSocketConnectionOrgHolder.nofityByKey(orgId,data);
    }

    public void notifyAll(String tableId,String orgId,String data){

        this.notifyTable(tableId,data);
        this.notifyOrg(orgId, data);

    }


}
