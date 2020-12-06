package cn.edu.xmu.order.model.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel(description = "数量视图对象1")
@Data
public class amountVo {
    @ApiModelProperty(name = "订单明细")
    private Long amount;

}
