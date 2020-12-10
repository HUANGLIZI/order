package cn.edu.xmu.payment.model.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "订单详情视图对象1")
@Data
public class amountVo {
    @ApiModelProperty(name = "数量")
    private Long amount;
}
