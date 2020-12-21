package cn.edu.xmu.payment.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.payment.model.po.RefundPo;
import cn.edu.xmu.payment.model.vo.RefundRetVo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Caixin
 * @date 2020-12-03 19:17
 */
public class RefundBo implements VoObject, Serializable {

    private Long id;

    private Long paymentId;

    private Long amount;

    private Byte state;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    private Long orderId;

    private Long aftersaleId;

    public RefundBo(RefundPo refundPo)
    {
        this.aftersaleId = refundPo.getAftersaleId();
        this.amount = refundPo.getAmount();
        this.gmtCreated = refundPo.getGmtCreate();
        this.gmtModified = refundPo.getGmtModified();
        this.id = refundPo.getId();
        this.orderId = refundPo.getOrderId();
        this.state = refundPo.getState();
        this.paymentId = refundPo.getPaymentId();
    }

    /**
     * 由Bo对象创建Vo对象
     */
    @Override
    public Object createVo(){
        return new RefundRetVo(this);
    }

    @Override
    public Object createSimpleVo(){
        return new RefundRetVo(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public LocalDateTime getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(LocalDateTime gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getAftersaleId() {
        return aftersaleId;
    }

    public void setAftersaleId(Long aftersaleId) {
        this.aftersaleId = aftersaleId;
    }
}
