package cn.edu.xmu.oomall.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Caixin
 * @date 2020-12-07 20:38
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO implements Serializable {
    private Long orderId;
    private String orderSn;
    private Long skuId;
    private String skuName;
    private Long shopId;
    /**
     * 订单详情中的商品单价
     */
    private Long price;
}
