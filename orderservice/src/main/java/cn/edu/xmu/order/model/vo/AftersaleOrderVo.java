package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.bo.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(description = "售后订单视图对象1")
@Data
public class AftersaleOrderVo {
    @ApiModelProperty(name = "订单明细")
    private List<OrderItemVo> orderItemVoList;
    @ApiModelProperty(name = "收货人")
    private String consignee;
    @ApiModelProperty(name = "地区ID")
    private Long regionId;
    @ApiModelProperty(name = "地址")
    private String address;
    @ApiModelProperty(name = "电话号码")
    private String mobile;
    @ApiModelProperty(name = "留言")
    private String message;
    /**
     * 构造函数
     *
     * @author 24320182203227 李子晗
     * @return Orders
     */
    public Orders createOrder() {
        Orders orders = new Orders();
//        orders.setId(id);
//        orders.setCustomerId(customerId);
//        orders.setShopId(shopId);
//        orders.setOrderSn(orderSn);
//        orders.setPid(pid);
        orders.setConsignee(consignee);
        orders.setRegionId(regionId);
        orders.setAddress(address);
        orders.setMobile(mobile);
        orders.setMessage(message);
//        orders.setOrderType(orderType);
//        orders.setFreightPrice(freightPrice);
//        orders.setCouponId(couponId);
//        orders.setCouponActivityId(couponActivityId);
//        orders.setDiscountPrice(discountPrice);
//        orders.setOriginPrice(originPrice);
//        orders.setPresaleId(presaleId);
//        orders.setGrouponDiscount(grouponDiscount);
//        orders.setRebateNum(rebateNum);
//        orders.setConfirmTime(confirmTime);
//        orders.setShipmentSn(shipmentSn);
//        orders.setState(state);
//        orders.setSubstate(substate);
//        orders.setBeDeleted(beDeleted);
//        orders.setGmtCreated(gmtCreated);
//        orders.setGmtModified(gmtModified);
        return orders;
    }
}
