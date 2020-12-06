package cn.edu.xmu.order.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.order.model.po.OrdersPo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单Bo类
 **/
@Data
public class Orders implements VoObject, Serializable {
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

    public Orders() {
    }

    /**
     * 构造函数
     *
     * @param po 用PO构造
     * @return Orders
     */
    public Orders(OrdersPo po) {
        this.id = po.getId();
        this.customerId=po.getCustomerId();
        this.shopId=po.getShopId();
        this.orderSn=po.getOrderSn();
        this.pid=po.getPid();
        this.consignee = po.getConsignee();
        this.regionId=po.getRegionId();
        this.address = po.getAddress();
        this.mobile=po.getMobile();
        this.message=po.getMessage();
        this.orderType=po.getOrderType();
        this.freightPrice=po.getFreightPrice();
        this.couponId=po.getCouponId();
        this.couponActivityId=po.getCouponActivityId();
        this.discountPrice=po.getDiscountPrice();
        this.originPrice=po.getOriginPrice();
        this.grouponDiscount=po.getGrouponDiscount();
        this.rebateNum=po.getRebateNum();
        this.confirmTime = po.getConfirmTime();
        this.shipmentSn=po.getShipmentSn();
        this.beDeleted = po.getBeDeleted();
        this.state=po.getState();
        this.gmtCreated = po.getGmtCreated();
        this.gmtModified = po.getGmtModified();
        this.presaleId=po.getPresaleId();
        this.substate=po.getSubstate();
    }

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
    public OrdersPo gotOrdersPo() {
        OrdersPo po = new OrdersPo();
        po.setId(id);
        po.setCustomerId(customerId);
        po.setShopId(shopId);
        po.setOrderSn(orderSn);
        po.setPid(pid);
        po.setConsignee(consignee);
        po.setRegionId(regionId);
        po.setAddress(address);
        po.setMobile(mobile);
        po.setMessage(message);
        po.setOrderType(orderType);
        po.setFreightPrice(freightPrice);
        po.setCouponId(couponId);
        po.setCouponActivityId(couponActivityId);
        po.setDiscountPrice(discountPrice);
        po.setOriginPrice(originPrice);
        po.setPresaleId(presaleId);
        po.setGrouponDiscount(grouponDiscount);
        po.setRebateNum(rebateNum);
        po.setConfirmTime(confirmTime);
        po.setShipmentSn(shipmentSn);
        po.setState(state);
        po.setSubstate(substate);
        po.setBeDeleted(beDeleted);
        po.setGmtCreated(gmtCreated);
        po.setGmtModified(gmtModified);
        return po;
    }
}
