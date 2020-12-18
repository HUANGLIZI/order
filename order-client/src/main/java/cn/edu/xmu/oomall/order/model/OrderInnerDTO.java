package cn.edu.xmu.oomall.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Caixin
 * @date 2020-12-07 20:38
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInnerDTO implements Serializable {
    private Long shopId;

    private Long customerId;

    private Long orderId;

    private Byte state;

    private Byte substate;

    private Long price;
}
