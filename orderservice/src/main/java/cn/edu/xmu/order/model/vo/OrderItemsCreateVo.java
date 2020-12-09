package cn.edu.xmu.order.model.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "订单详情视图对象1")
@Data
public class OrderItemsCreateVo {

    @NotNull
    @ApiModelProperty(name = "SKU商品ID")
    private Long skuId;

    @ApiModelProperty(name = "数量")
    private Integer quantity;

    @ApiModelProperty(name = "优惠券活动Id")
    private Long couponActId;

    public Long getGoodsSkuId() {
        return skuId;
    }

    public void setGoodsSkuId(Long goodsSkuId) {
        this.skuId = goodsSkuId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getCouponActivityId() {
        return couponActId;
    }

    public void setCouponActivityId(Long couponActivityId) {
        this.couponActId = couponActivityId;
    }
}