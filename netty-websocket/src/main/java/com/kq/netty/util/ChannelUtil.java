package com.kq.netty.util;

import com.google.common.base.Preconditions;
import com.kq.netty.constants.Constants;
import com.kq.netty.dto.UpgradeDataDto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;

/**
 * @author kq
 * @date 2021-06-21 13:59
 * @since 2020-0630
 */
public class ChannelUtil {

    public static void setUpgradeDto(ChannelHandlerContext ctx, UpgradeDataDto dto) {

        Preconditions.checkArgument(dto!=null,"UpgradeDataDto is null !");
        Preconditions.checkNotNull(dto.getOrgId(),"UpgradeDataDto orgId is null !");
        Preconditions.checkNotNull(dto.getTableId(),"UpgradeDataDto tableId is null !");
        Preconditions.checkNotNull(dto.getCreateTime(),"UpgradeDataDto createTime is null");


        Attribute<UpgradeDataDto> attr = ctx.channel().attr(Constants.BXDCWS_UPGRADE_KEY);
        UpgradeDataDto tokenAttr = attr.get();
        if (tokenAttr == null) {
            attr.set(dto);
            attr.setIfAbsent(dto);
//            tokenAttr = attr.setIfAbsent(dto);
        } else {
            System.out.println("UpgradeDataDto 中是有值的");
        }
    }

    /**
     * 获取token
     * @param ctx
     * @return
     */
    public static UpgradeDataDto getUpgradeDto(ChannelHandlerContext ctx) {

        Attribute<UpgradeDataDto> attr = ctx.channel().attr(Constants.BXDCWS_UPGRADE_KEY);
        UpgradeDataDto tokenAttr = attr.get();
        return tokenAttr;
    }

}
