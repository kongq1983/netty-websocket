package com.kq.netty.connection;

/**
 * key: orgId
 * @author kq
 * @date 2021-06-21 15:38
 * @since 2020-0630
 */
public class WebSocketConnectionOrgHolder extends WebSocketConnectionBase{


    @Override
    public String getType() {
        return "org";
    }
}
