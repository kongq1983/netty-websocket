package com.kq.netty.connection;

/**
 * key: tableId
 * @author kq
 * @date 2021-06-21 15:38
 * @since 2020-0630
 */
public class WebSocketConnectionTableHolder extends WebSocketConnectionBase {

    @Override
    public String getType() {
        return "table";
    }

}
