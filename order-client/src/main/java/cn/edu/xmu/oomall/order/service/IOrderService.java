package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.OrderDTO;
import cn.edu.xmu.oomall.order.model.OrderInnerDTO;

import java.util.List;

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
    ReturnObject<OrderInnerDTO> findUserIdbyOrderId(Long orderId);

    ReturnObject<OrderInnerDTO> findShopIdbyOrderId(Long orderId);

    ReturnObject<OrderDTO> getSelectOrderInfo(Long userId, Long orderItemId);

    ReturnObject<List<Long>> listUserSelectOrderItemId(Long userId, Long skuId);

    ReturnObject<List<Long>> listAdminSelectOrderItemId(Long shopId,Long skuId);

    ReturnObject<Boolean> isOrderBelongToShop(Long shopId, Long orderId);
}
