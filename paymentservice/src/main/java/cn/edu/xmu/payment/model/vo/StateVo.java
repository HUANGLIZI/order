package cn.edu.xmu.payment.model.vo;

import cn.edu.xmu.payment.model.bo.Payment;

public class StateVo
{
    private Long Code;
    private String name;
    public StateVo(Payment.State state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }

    public Long getCode() {
        return Code;
    }

    public String getName() {
        return name;
    }

    public void setCode(Long code) {
        Code = code;
    }

    public void setName(String name) {
        this.name = name;
    }
}
