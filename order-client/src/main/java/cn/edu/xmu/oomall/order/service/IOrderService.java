package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.OrderDTO;

/**
 * @author Caixin
 * @date 2020-12-05 21:37
 */
public interface IOrderService {

    /**
     * 通过订单id查找用户id
     * @param orderId
     * @return Long
     * @author Cai Xinlu
     * @date 2020-12-05 21:38
     */
    ReturnObject<OrderDTO> findUserIdbyOrderId(Long orderId);

    ReturnObject<OrderDTO> findShopIdbyOrderId(Long orderId);
}
