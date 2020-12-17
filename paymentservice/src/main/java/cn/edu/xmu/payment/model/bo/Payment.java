package cn.edu.xmu.payment.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.payment.model.po.PaymentPo;
import cn.edu.xmu.payment.model.vo.PaymentRetVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class Payment implements VoObject, Serializable {


    private Long id;

    private Long amount;

    private Long actualAmount;

    private String paymentPattern;

    private LocalDateTime payTime;

    private String paySn;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private Long orderId;

    private Long aftersaleId;

    private Byte state;

    private LocalDateTime gmtCreate;

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
        this.amount=po.getAmount();
        this.beginTime=po.getBeginTime();
        this.endTime=po.getEndTime();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
        this.id=po.getId();
        this.orderId=po.getOrderId();
        this.paymentPattern=po.getPaymentPattern();
        this.paySn=po.getPaySn();
        this.payTime=po.getPayTime();
        this.state=po.getState();
        this.aftersaleId=po.getAftersaleId();
    }

    /**
     * 获得po
     *
     * @return Payment
     */
    public PaymentPo getPaymentPo(){
        PaymentPo paymentPo = new PaymentPo();
        paymentPo.setId(this.id);
        paymentPo.setActualAmount(this.actualAmount);
        paymentPo.setAmount(this.amount);
        paymentPo.setBeginTime(this.beginTime);
        paymentPo.setEndTime(this.endTime);
        paymentPo.setOrderId(this.orderId);
        paymentPo.setPaymentPattern(this.paymentPattern);
        paymentPo.setPaySn(this.paySn);
        paymentPo.setPayTime(this.payTime);
        paymentPo.setGmtCreate(this.gmtCreate);
        paymentPo.setGmtModified(this.gmtModified);
        paymentPo.setState(this.state);
        paymentPo.setAftersaleId(this.aftersaleId);
        return paymentPo;
    }
    @Override
    public Object createVo() {
        return new PaymentRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

    public enum State {
        TO_BE_PAID(0, "未支付"),
        HAS_PAID(1, "已支付"),
        Failure(2, "支付失败");

        private static final Map<Integer, State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Payment.State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Payment.State getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }


}
