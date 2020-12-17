package cn.edu.xmu.order.model.bo;

import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.vo.OrderItemsCreateVo;
import lombok.Data;

/**
 * @author Caixin
 * @date 2020-12-08 15:39
 */
@Data
public class OrderItems {
    private Long Id;
    private Long skuId;
    private Long orderId;
    private String name;
    private Integer quantity;
    private Long discount;
    private Long price;
    private Long couponActId;
    private Long beShareId;

    public OrderItems(OrderItemsCreateVo orderItemsCreateVo)
    {
        this.skuId = orderItemsCreateVo.getGoodsSkuId();
        this.quantity = orderItemsCreateVo.getQuantity();
        this.couponActId = orderItemsCreateVo.getCouponActivityId();
    }

    public OrderItems(OrderItemPo orderItemPo)
    {
        this.Id = orderItemPo.getId();
        this.skuId = orderItemPo.getGoodsSkuId();
        this.couponActId = orderItemPo.getCouponActivityId();
        this.quantity = orderItemPo.getQuantity();
        this.beShareId = orderItemPo.getBeShareId();
        this.discount = orderItemPo.getDiscount();
        this.name = orderItemPo.getName();
        this.orderId = orderItemPo.getOrderId();
        this.price = orderItemPo.getPrice();
    }

    public OrderItemPo gotOrderItemPo()
    {
        OrderItemPo orderItemPo = new OrderItemPo();
        orderItemPo.setId(this.Id);
        orderItemPo.setBeShareId(this.beShareId);
        orderItemPo.setCouponActivityId(this.couponActId);
        orderItemPo.setPrice(this.price);
        orderItemPo.setDiscount(this.discount);
        orderItemPo.setName(this.name);
        orderItemPo.setOrderId(this.orderId);
        orderItemPo.setGoodsSkuId(this.skuId);
        orderItemPo.setQuantity(this.quantity);
        return orderItemPo;
    }
}
