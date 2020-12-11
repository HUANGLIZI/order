package cn.edu.xmu.freight.model.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel(description = "订单详情视图对象1")
@Data
public class OrderItemVo {
    @ApiModelProperty(name = "模板名")
    private Long skuId;

    @ApiModelProperty(name = "数量")
    private Integer count;

    public Integer getConut() {
        return  this.count;
    }

    public Long getSkuId() {
        return this.skuId;
    }
}
