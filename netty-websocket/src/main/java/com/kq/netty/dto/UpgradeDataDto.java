package com.kq.netty.dto;

import java.util.Date;

/**
 * @author kq
 * @date 2021-06-21 15:05
 * @since 2020-0630
 */
public class UpgradeDataDto {

    private String orgId;
    private String tableId;

    /**
     * 连接建立时间
     */
    private Date createTime;
    private Date updateTime;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "UpgradeDataDto{" +
                "orgId='" + orgId + '\'' +
                ", tableId='" + tableId + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
