package cn.edu.xmu.payment.model.vo;

import cn.edu.xmu.payment.model.bo.RefundBo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Caixin
 * @date 2020-11-30 20:06
 */

@Data
@ApiModel(description = "买家查询自己的退款信息")
public class RefundRetVo {

    @ApiModelProperty(value = "")
    private Long id;

    @ApiModelProperty(value = "")
    private Long paymentId;

    @ApiModelProperty(value = "")
    private Long amount;

    @ApiModelProperty(value = "")
    private Byte state;

    @ApiModelProperty(value = "")
    private LocalDateTime gmtCreated;

    @ApiModelProperty(value = "")
    private LocalDateTime gmtModified;

    @ApiModelProperty(value = "")
    private Long orderId;

    @ApiModelProperty(value = "")
    private Long aftersaleId;

    public RefundRetVo(RefundBo refundBo)
    {
        this.aftersaleId = refundBo.getId();
        this.amount = refundBo.getAmount();
        this.gmtCreated = refundBo.getGmtCreated();
        this.gmtModified = refundBo.getGmtModified();
        this.id = refundBo.getId();
        this.orderId = refundBo.getOrderId();
        this.state = refundBo.getState();
        this.paymentId = refundBo.getPaymentId();
    }
}
