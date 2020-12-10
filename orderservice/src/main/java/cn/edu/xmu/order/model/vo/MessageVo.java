package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.bo.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
* 订单留言修改视图
*
**/

@Data
@ApiModel(description = "订单留言修改视图")
public class MessageVo {

    @NotBlank
    @ApiModelProperty(value = "留言")
    String message;

    /**
     * 通过vo构造bo
     * @return
     */
    public Orders createOrder(){
        Orders orders = new Orders();
        orders.setMessage(this.message);

        return orders;
    }
}
