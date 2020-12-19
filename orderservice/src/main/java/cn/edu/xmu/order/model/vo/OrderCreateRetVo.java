package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.bo.OrderItems;
import cn.edu.xmu.order.model.bo.Orders;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Caixin
 * @date 2020-12-08 23:21
 */
@Data
public class OrderCreateRetVo implements VoObject, Serializable {

    private Long id;

    private Long pid;

    private String consignee;

    private Long regionId;

    private String address;

    private String mobile;

    private String message;

    private Byte orderType;

    private Long freightPrice;

    private Long couponId;

    private Long discountPrice;

    private Long originPrice;

    private Long presaleId;

    private Integer rebateNum;

    private LocalDateTime confirmTime;

    private String shipmentSn;

    private Byte state;

    private Byte subState;

    private Long grouponId;

    private String orderSn;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private List<OrderItems> orderItems;

    private CustomerRetVo customer;

    private ShopRetVo shop;

    public OrderCreateRetVo(Orders orders,CustomerRetVo customerRetVo,ShopRetVo shopRetVo) {
        this.id = orders.getId();
        this.pid=orders.getPid();
        this.consignee = orders.getConsignee();
        this.regionId=orders.getRegionId();
        this.address = orders.getAddress();
        this.mobile=orders.getMobile();
        this.message=orders.getMessage();
        this.orderType=orders.getOrderType();
        this.freightPrice=orders.getFreightPrice();
        this.couponId=orders.getCouponId();
        this.discountPrice=orders.getDiscountPrice();
        this.originPrice=orders.getOriginPrice();
        this.rebateNum=orders.getRebateNum();
        this.confirmTime = orders.getConfirmTime();
        this.shipmentSn=orders.getShipmentSn();
        this.state=orders.getState();
        this.gmtCreate = orders.getGmtCreated();
        this.gmtModified = orders.getGmtModified();
        this.presaleId=orders.getPresaleId();
        this.subState=orders.getSubstate();
        this.grouponId = orders.getGrouponId();
        this.orderItems = orders.getOrderItemsList();
        this.customer = customerRetVo;
        this.shop = shopRetVo;
        this.orderSn = orders.getOrderSn();
    }

    @Override
    public Object createVo() {
        return this;
    }
}
