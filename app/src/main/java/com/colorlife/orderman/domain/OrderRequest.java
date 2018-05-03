package com.colorlife.orderman.domain;

import java.util.List;

/**
 * Created by ym on 2018/4/30.
 */

public class OrderRequest {
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
    //类型 1：有餐位  2：无餐位
    private Integer type;
    //桌子id
    private Integer deskId;
    //桌子名称
    private String deskName;
    //支付方式
    private String paywayValue;
    //支付名称
    private String paywayName;
    //备注
    private String remark;
    //用户id
    private Integer userId;
    //订单详情
    private List<OrderDetailRequest> orderDetailRequests;

    public Integer getId() {
        return id;
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

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public Double getSaleTotal() {
        return saleTotal;
    }

    public void setSaleTotal(Double saleTotal) {
        this.saleTotal = saleTotal;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getDeskId() {
        return deskId;
    }

    public void setDeskId(Integer deskId) {
        this.deskId = deskId;
    }

    public String getDeskName() {
        return deskName;
    }

    public void setDeskName(String deskName) {
        this.deskName = deskName;
    }

    public String getPaywayValue() {
        return paywayValue;
    }

    public void setPaywayValue(String paywayValue) {
        this.paywayValue = paywayValue;
    }

    public String getPaywayName() {
        return paywayName;
    }

    public void setPaywayName(String paywayName) {
        this.paywayName = paywayName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<OrderDetailRequest> getOrderDetailRequests() {
        return orderDetailRequests;
    }

    public void setOrderDetailRequests(List<OrderDetailRequest> orderDetailRequests) {
        this.orderDetailRequests = orderDetailRequests;
    }
}
