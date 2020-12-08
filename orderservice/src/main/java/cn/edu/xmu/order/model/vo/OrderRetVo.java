package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.bo.Orders;
import cn.edu.xmu.order.model.po.OrderItemPo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@ApiModel(description = "订单详情视图对象1")
@Data
public class OrderRetVo {
    private Long id;
    private Long customerId;
    private Long shopId;
    private String orderSn;
    private Long pid;
    private String consignee;
    private Long regionId;
    private String address;
    private String mobile;
    private String message;
    private Byte orderType;
    private Long freightPrice;
    private Long couponId;
    private Long couponActivityId;
    private Long discountPrice;
    private Long originPrice;
    private Long presaleId;
    private Long grouponDiscount;
    private Integer rebateNum;
    private LocalDateTime confirmTime;
    private String shipmentSn;
    private Byte state;
    private Byte substate;
    private Byte beDeleted;
    private LocalDateTime gmtCreated;
    private LocalDateTime gmtModified;
    private List<OrderItemPo> orderItems;

    /**
     * 用Order对象建立Vo对象
     */
    public OrderRetVo(Orders orders,List<OrderItemPo> orderItemPos) {
        this.id = orders.getId();
        this.customerId=orders.getCustomerId();
        this.shopId=orders.getShopId();
        this.orderSn=orders.getOrderSn();
        this.pid=orders.getPid();
        this.consignee = orders.getConsignee();
        this.regionId=orders.getRegionId();
        this.address = orders.getAddress();
        this.mobile=orders.getMobile();
        this.message=orders.getMessage();
        this.orderType=orders.getOrderType();
        this.freightPrice=orders.getFreightPrice();
        this.couponId=orders.getCouponId();
        this.couponActivityId=orders.getCouponActivityId();
        this.discountPrice=orders.getDiscountPrice();
        this.originPrice=orders.getOriginPrice();
        this.grouponDiscount=orders.getGrouponDiscount();
        this.rebateNum=orders.getRebateNum();
        this.confirmTime = orders.getConfirmTime();
        this.shipmentSn=orders.getShipmentSn();
        this.beDeleted = orders.getBeDeleted();
        this.state=orders.getState();
        this.gmtCreated = orders.getGmtCreated();
        this.gmtModified = orders.getGmtModified();
        this.presaleId=orders.getPresaleId();
        this.substate=orders.getSubstate();
        this.orderItems=orderItemPos;
    }


    public OrderRetVo(Orders orders) {
        this.id = orders.getId();
        this.customerId=orders.getCustomerId();
        this.shopId=orders.getShopId();
        this.orderSn=orders.getOrderSn();
        this.pid=orders.getPid();
        this.consignee = orders.getConsignee();
        this.regionId=orders.getRegionId();
        this.address = orders.getAddress();
        this.mobile=orders.getMobile();
        this.message=orders.getMessage();
        this.orderType=orders.getOrderType();
        this.freightPrice=orders.getFreightPrice();
        this.couponId=orders.getCouponId();
        this.couponActivityId=orders.getCouponActivityId();
        this.discountPrice=orders.getDiscountPrice();
        this.originPrice=orders.getOriginPrice();
        this.grouponDiscount=orders.getGrouponDiscount();
        this.rebateNum=orders.getRebateNum();
        this.confirmTime = orders.getConfirmTime();
        this.shipmentSn=orders.getShipmentSn();
        this.beDeleted = orders.getBeDeleted();
        this.state=orders.getState();
        this.gmtCreated = orders.getGmtCreated();
        this.gmtModified = orders.getGmtModified();
        this.presaleId=orders.getPresaleId();
        this.substate=orders.getSubstate();
    }
}
