package cn.edu.xmu.payment.model.vo;

import cn.edu.xmu.payment.model.bo.Payment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentRetVo {

    @ApiModelProperty(value = "支付单id")
    private Long id;

    @ApiModelProperty(value = "付款金额")
    private Long amout;

    @ApiModelProperty(value = "实际付款金额")
    private Long actualAmount;

    @ApiModelProperty(value = "支付方式")
    private Byte paymentPattern;

    @ApiModelProperty(value = "支付时间")
    private LocalDateTime payTime;

    @ApiModelProperty(value = "支付编号")
    private String paySn;

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
    private LocalDateTime gmtCreated;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    public PaymentRetVo(Payment payment) {
        this.actualAmount=payment.getActualAmount();
        this.amout=payment.getAmout();
        this.beginTime=payment.getBeginTime();
        this.endTime=payment.getEndTime();
        this.gmtCreated=payment.getGmtCreated();
        this.gmtModified=payment.getGmtModified();
        this.id=payment.getId();
        this.orderId=payment.getOrderId();
        this.paymentPattern=payment.getPaymentPattern();
        this.paySn=payment.getPaySn();
        this.payTime=payment.getPayTime();
        this.state=payment.getState();
        this.aftersaleId=payment.getAftersaleId();
    }


}