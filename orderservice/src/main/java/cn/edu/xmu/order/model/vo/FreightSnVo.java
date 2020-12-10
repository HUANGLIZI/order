package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.bo.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "订单运输sn修改视图")
public class FreightSnVo {
    @NotBlank
    @ApiModelProperty(value = "订单运输sn")
    String freightSn;

    /**
     * 通过vo构造bo
     * @return
     */
    public Orders createOrder(){
        Orders orders = new Orders();
        orders.setShipmentSn(this.freightSn);

        return orders;
    }
}
