package cn.edu.xmu.payment.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.payment.model.po.PaymentPo;
import cn.edu.xmu.payment.model.vo.PaymentRetVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Payment implements VoObject, Serializable {


    private Long id;


    private Long amout;


    private Long actualAmount;


    private Byte paymentPattern;

    private LocalDateTime payTime;

    private String paySn;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private Long orderId;

    private Long aftersaleId;

    private Byte state;


    private LocalDateTime gmtCreated;


    private LocalDateTime gmtModified;
    public Payment(){

    }

    /**
     * 构造函数
     *
     * @param po 用PO构造
     * @return Payment
     */
    public Payment(PaymentPo po){
        this.actualAmount=po.getActualAmount();
        this.amout=po.getAmout();
        this.beginTime=po.getBeginTime();
        this.endTime=po.getEndTime();
        this.gmtCreated=po.getGmtCreated();
        this.gmtModified=po.getGmtModified();
        this.id=po.getId();
        this.orderId=po.getOrderId();
        this.paymentPattern=po.getPaymentPattern();
        this.paySn=po.getPaySn();
        this.payTime=po.getPayTime();
        this.state=po.getState();
    }

    @Override
    public Object createVo() {
        return new PaymentRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
