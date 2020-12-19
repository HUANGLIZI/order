package cn.edu.xmu.payment.model.vo;

import cn.edu.xmu.payment.model.bo.Payment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentRetVo{

    @ApiModelProperty(value = "支付单id")
    private Long id;

    @ApiModelProperty(value = "付款金额")
    private Long amount;

    @ApiModelProperty(value = "实际付款金额")
    private Long actualAmount;

    @ApiModelProperty(value = "支付方式")
    private String paymentPattern;

    @ApiModelProperty(value = "支付时间")
    private LocalDateTime payTime;

    @ApiModelProperty(value = "开始支付时间")
    private LocalDateTime beginTime;

    @ApiModelProperty(value = "终止支付时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "对应订单id")
    private Long orderId;

    @ApiModelProperty(value = "对应售后单id")
    private Long aftersaleId;

    @ApiModelProperty(value = "支付状态")
    private Byte state;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    public PaymentRetVo(Payment payment) {
        this.actualAmount=payment.getActualAmount();
        this.amount=payment.getAmount();
        this.beginTime=payment.getBeginTime();
        this.endTime=payment.getEndTime();
        this.gmtCreate=payment.getGmtCreate();
        this.gmtModified=payment.getGmtModified();
        this.id=payment.getId();
        this.orderId=payment.getOrderId();
        this.paymentPattern=payment.getPaymentPattern();
        this.payTime=payment.getPayTime();
        this.state=payment.getState();
        this.aftersaleId=payment.getAftersaleId();
    }


}
