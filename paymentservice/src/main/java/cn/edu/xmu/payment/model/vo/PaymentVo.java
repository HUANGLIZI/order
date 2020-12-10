package cn.edu.xmu.payment.model.vo;

import cn.edu.xmu.payment.model.bo.Payment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 支付单视图
 *
 **/
@Data
@ApiModel(description = "支付单视图对象")
public class PaymentVo {
    @NotNull
    @ApiModelProperty(value = "支付价格")
    private Long price;

    @NotBlank
    @ApiModelProperty(value = "支付方式")
    private String paymentPattern;

    /**
     * 通过vo构造bo
     * @return
     */
    public Payment createPayment(){
        Payment payment = new Payment();
        payment.setAmount(this.price);
        payment.setActualAmount(this.price);
        payment.setPaymentPattern(this.paymentPattern);


        return payment;
    }
}
