package cn.edu.xmu.order.model.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel(description = "订单详情视图对象1")
@Data
public class OrderItemVo {
    @ApiModelProperty(name = "SKU商品ID")
    private String skuId;

    @ApiModelProperty(name = "数量")
    private Integer count;

    public Integer getConut() {
        return  this.count;
    }

    public String getSkuId() {
        return this.skuId;
    }
}
