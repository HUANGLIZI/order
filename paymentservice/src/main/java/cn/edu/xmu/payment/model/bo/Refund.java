package cn.edu.xmu.payment.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.payment.model.po.RefundPo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 退款Bo类
 **/
@Data
public class Refund implements VoObject, Serializable {
    private Long id;
    private Long paymentId;
    private Long amount;
    private Long orderId;
    private Byte state;
    private LocalDateTime gmtCreate;
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
        this.orderId = po.getOrderId();
        this.state = po.getState();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
        this.aftersaleId = po.getAftersaleId();
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
        po.setOrderId(orderId);
        po.setState(state);
        po.setGmtModified(gmtModified);
        po.setGmtCreate(gmtCreate);
        po.setAftersaleId(aftersaleId);
        return po;
    }

    public enum State {
        TO_BE_REFUND(0, "未退款"),
        HAS_REFUND(1, "已退款"),
        REJECT_REFUND(2, "拒绝退款");

        private static final Map<Integer, Refund.State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Refund.State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Refund.State getTypeByCode(Integer code) {
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
