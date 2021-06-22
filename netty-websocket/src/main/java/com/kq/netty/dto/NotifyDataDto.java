package com.kq.netty.dto;

/**
 * @author kq
 * @date 2021-06-21 17:58
 * @since 2020-0630
 */
public class NotifyDataDto {

    private String id;

    /*1: 加菜  2: 减菜  3: 删菜 **/
    private int type;

    /**
     * 购物车现在数量
     */
    private int size;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "NotifyDataDto{" +
                "type=" + type +
                ", size=" + size +
                '}';
    }
}
