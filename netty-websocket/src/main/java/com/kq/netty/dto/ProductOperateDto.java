package com.kq.netty.dto;

/**
 * @author kq
 * @date 2021-06-22 16:59
 * @since 2020-0630
 */
public class ProductOperateDto {

    private String id;
    private int size;
    /**0: 减菜  1:加菜*/
    private int type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ProductOperateDto{" +
                "id='" + id + '\'' +
                ", size=" + size +
                ", type=" + type +
                '}';
    }
}
