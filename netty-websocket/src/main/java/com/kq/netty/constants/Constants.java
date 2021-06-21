package com.kq.netty.constants;

import com.kq.netty.dto.UpgradeDataDto;
import io.netty.util.AttributeKey;

/**
 * @author kq
 * @date 2021-06-21 8:48
 * @since 2020-0630
 */
public class Constants {

    /**
     * 包厢点餐ws
     */
    public static final String WEBSOCKET_PATH = "/bxdcws";

    /**
     * 包厢点餐 UPGRADE
     */
    public static final AttributeKey<UpgradeDataDto> BXDCWS_UPGRADE_KEY = AttributeKey.valueOf("shr.bxdcws.upgrade");

    /**
     * 机构ID
     */
    public static final AttributeKey<String> BXDCWS_ORGID_KEY = AttributeKey.valueOf("shr.bxdcws.orgid");

//    public static final HttpResponseStatus UNAUTHORIZED = newStatus(401, "Unauthorized");

//    private static HttpResponseStatus newStatus(int statusCode, String reasonPhrase) {
//        return new HttpResponseStatus(statusCode, reasonPhrase, true);
//    }

}
