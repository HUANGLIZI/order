package cn.edu.xmu.payment.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.payment.model.po.RefundPo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 退款Bo类
 **/
@Data
public class Refund implements VoObject, Serializable {
    private Long id;
    private Long paymentId;
    private Long amount;
    private String paySn;
    private Long orderId;
    private Byte state;
    private LocalDateTime gmtCreated;
    private LocalDateTime gmtModified;
    private Long aftersaleId;

    public Refund() {
    }

    /**
     * 构造函数
     */
    public Refund(RefundPo po) {
        this.id = po.getId();
        this.paymentId = po.getPaymentId();
        this.amount = po.getAmount();
        this.paySn = po.getPaySn();
        this.orderId = po.getOrderId();
        this.state=po.getState();
        this.gmtCreated = po.getGmtCreated();
        this.gmtModified = po.getGmtModified();
        this.aftersaleId=po.getAftersaleId();
    }

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
    public RefundPo gotRefundPo() {
        RefundPo po = new RefundPo();
        po.setId(id);
        po.setPaymentId(paymentId);
        po.setAmount(amount);
        po.setPaySn(paySn);
        po.setOrderId(orderId);
        po.setState(state);
        po.setGmtModified(gmtModified);
        po.setGmtCreated(gmtCreated);
        po.setAftersaleId(aftersaleId);
        return po;
    }
}
