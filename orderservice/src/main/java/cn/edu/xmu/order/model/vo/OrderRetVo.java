package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.order.model.bo.OrderItems;
import cn.edu.xmu.order.model.bo.Orders;
import cn.edu.xmu.order.model.po.OrderItemPo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel(description = "订单详情视图对象1")
@Data
public class OrderRetVo implements VoObject, Serializable {
    private Long id;
    private Long customerId;
    private Long shopId;
    private Long pid;
    private Byte orderType;
    private Byte state;
    private Byte subState;
    private LocalDateTime gmtCreate;
    private Long originPrice;
    private Long discountPrice;
    private Long freightPrice;
    private Long grouponId;
    private Long presaleId;
    private String shipmentSn;


    /**
     * 用Order对象建立Vo对象
     */
//    public OrderRetVo(Orders orders,List<OrderItems> orderItems,CustomerRetVo customerRetVo,ShopRetVo shopRetVo) {
//        this.id = orders.getId();
//        this.customerRetVo=customerRetVo;
//        this.shopRetVo=shopRetVo;
//        this.orderSn=orders.getOrderSn();
//        this.pid=orders.getPid();
//        this.consignee = orders.getConsignee();
//        this.regionId=orders.getRegionId();
//        this.address = orders.getAddress();
//        this.mobile=orders.getMobile();
//        this.message=orders.getMessage();
//        this.orderType=orders.getOrderType();
//        this.freightPrice=orders.getFreightPrice();
//        this.couponId=orders.getCouponId();
//        this.couponActivityId=orders.getCouponActivityId();
//        this.discountPrice=orders.getDiscountPrice();
//        this.originPrice=orders.getOriginPrice();
//        this.grouponDiscount=orders.getGrouponDiscount();
//        this.rebateNum=orders.getRebateNum();
//        this.confirmTime = orders.getConfirmTime();
//        this.shipmentSn=orders.getShipmentSn();
//        this.beDeleted = orders.getBeDeleted();
//        this.state=orders.getState();
//        this.gmtCreated = orders.getGmtCreated();
//        this.gmtModified = orders.getGmtModified();
//        this.presaleId=orders.getPresaleId();
//        this.substate=orders.getSubstate();
//        this.orderItems=orderItems;
//    }


    public OrderRetVo(Orders orders) {
        this.id = orders.getId();
        this.pid=orders.getPid();
        this.customerId = orders.getCustomerId();
        this.shopId = orders.getShopId();
        this.orderType=orders.getOrderType();
        this.freightPrice=orders.getFreightPrice();
        this.discountPrice=orders.getDiscountPrice();
        this.originPrice=orders.getOriginPrice();
        this.shipmentSn=orders.getShipmentSn();
        this.state=orders.getState();
        this.gmtCreate = orders.getGmtCreated();
        this.presaleId=orders.getPresaleId();
        this.subState=orders.getSubstate();
        this.grouponId=orders.getGrouponId();
    }


    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
