package cn.edu.xmu.payment.model.vo;

public class PayPatternVo {
    private String payPattern;
    private String name;
    public PayPatternVo(String payPattern, String name)
    {
        this.payPattern = payPattern;
        this.name = name;
    }

    public String getPayPattern() {
        return payPattern;
    }

    public String getName() {
        return name;
    }
}
