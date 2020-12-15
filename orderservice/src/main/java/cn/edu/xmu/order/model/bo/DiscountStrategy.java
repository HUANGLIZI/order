package cn.edu.xmu.order.model.bo;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DiscountStrategy{

    /**
     * JSON格式
     *{"type":“xxx","threshold":"xxx","discountoff":"xxx"}
     * 说明：
     * type:0——满金额减，1——满金额打折，2——满件数减，3——满件数打折
     * threshold:门槛值(金额或件数)
     * discountoff:优惠量（金额或折扣）
     *
     * 举例：
     * type为0——{type:"0",threshold:"100",discountoff:"20"}满100减20
     * type为1——{type:"1",threshold:"100",discountoff:"20"}满100减20%
     * type为2——{type:"2",threshold:"2",discountoff:"20"}满2件减20
     * type为3——{type:"3",threshold:"2",discountoff:"20"}满2件减20%
     */


    /**
     * 优惠类型
     */
    private Integer type;
    /**
     *门槛值
     */
    private Long threshold;
    /**
     * 优惠量
     */
    private Long discountoff;


    public Long getThreshold() {
        return threshold;
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    public Long getDiscountoff() {
        return discountoff;
    }

    public void setDiscountoff(Long offCash) {
        this.discountoff = offCash;
    }

}
