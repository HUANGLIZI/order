package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.bo.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Caixin
 * @date 2020-12-06 21:22
 */
@Data
@ApiModel(description = "创建订单视图")
public class OrdersVo {

    @NotNull
    @ApiModelProperty(name = "订单详情列表")
    private List<OrderItemsCreateVo> orderItems;

    @ApiModelProperty(name = "收件人")
    private String consignee;

    @ApiModelProperty(name = "地区Id")
    private Long regionId;

    @ApiModelProperty(name = "详细地址")
    private String address;

    @ApiModelProperty(name = "联系方式")
    private String mobile;

    @ApiModelProperty(name = "留言")
    private String message;

    @ApiModelProperty(name = "优惠券Id")
    private Long couponId;

    @ApiModelProperty(name = "预售Id")
    private Long presaleId;

    @ApiModelProperty(name = "团购Id")
    private Long grouponId;

    public Orders createOrdersBo()
    {
        Orders orders = new Orders();
        orders.setGrouponId(this.grouponId);
        orders.setPresaleId(this.presaleId);
        orders.setCouponId(this.couponId);
        orders.setConsignee(this.consignee);
        orders.setRegionId(this.regionId);
        orders.setAddress(this.address);
        orders.setMobile(this.mobile);
        orders.setMessage(this.message);
        return orders;
    }
}
