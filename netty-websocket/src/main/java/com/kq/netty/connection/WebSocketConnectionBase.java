package com.kq.netty.connection;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author kq
 * @date 2021-06-21 15:39
 * @since 2020-0630
 */
public abstract class WebSocketConnectionBase {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConnectionBase.class);

    /**
     * 通知全部
     */
    public static final int NOFITY_ALL = 0;

    /**
     * 根据通知桌子
     */
    public static final int NOFITY_TABLE = 1;
    public static final int NOFITY_ORG = 2;

    protected ConcurrentMap<String, Set<Channel>> channelMap = new ConcurrentHashMap<>();


    /**
     * 添加通道
     * @param key
     * @param val
     */
    protected void addChannel(final String key, final Channel val) {
        channelMap.computeIfAbsent(key, k -> new ConcurrentSkipListSet<>()).add(val);
    }


    protected void deleteChannel(String key,String channelId) {

        Set<Channel> channels = channelMap.get(key);

        if(CollectionUtils.isEmpty(channels)) {
            logger.warn("删除通道失败！ 没有找到通道！channelId={}",channelId);
            return;
        }

        boolean isFind = false;
        for(Channel channel : channels) {
            if(channel.id().asLongText().equals(channelId)) {
                boolean delete = channels.remove(channel);
                logger.debug("删除通道结果-1 type={} key={} delete={}, channelId={}",this.getType(),key,delete,channelId);
                isFind = delete;
                break;
            }
        }

        logger.debug("删除通道结果-2 key={} isFind={}, channelId={}",key,isFind,channelId);

    }

    /**
     * 通知某个key下的所有通道(比如某张桌子、某个餐厅)
     * @param key
     * @param data
     */
    protected void nofityByKey(final String key,String data) {

        Set<Channel> channels = getChannel(key);

        if(CollectionUtils.isEmpty(channels)) {
            logger.warn("通知找不到通道! key={}",key);
            return;
        }

        for(Channel channel : channels) {

            if (!channel.isActive()) {
                logger.warn("非正常通道状态! 当前通道状态active={}, channelId={}", channel.isActive(), channel.id().asLongText());
                return;
            }

            channel.writeAndFlush(new TextWebSocketFrame(data));
        }


    }

    protected Set<Channel> getChannel(String key) {
        Set<Channel> channels = channelMap.getOrDefault(key, Collections.emptySet());
        return channels;
    }


    protected Channel getChannel(String key , String channelId) {

        Set<Channel> channels = channelMap.get(key);
        for(Channel channel : channels) {
            if(channel.id().asLongText().equals(channelId)) {
                return channel;
            }
        }

        return null;
    }

    public abstract String getType();

}
