package com.colorlife.orderman.domain;

/**
 * Created by ym on 2018/4/22.
 */

public class OrderListVo {
    private Integer id;
    //订单号
    private String number;
    //创建时间
    private String createTime;
    //下单时间
    private String orderTime;
    //总价
    private Double saleTotal;
    //状态
    private Integer status;
    //类型
    private Integer type;
    //桌子id
    private Integer deskId;
    //桌子名称
    private String deskName;
    //备注
    private String remark;

    public String getDeskName() {
        return deskName;
    }
    public void setDeskName(String deskName) {
        this.deskName = deskName;
    }
    public Integer getId() {
        return id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Double getSaleTotal() {
        return saleTotal;
    }

    public void setSaleTotal(Double saleTotal) {
        this.saleTotal = saleTotal;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDeskId() {
        return deskId;
    }

    public void setDeskId(Integer deskId) {
        this.deskId = deskId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
