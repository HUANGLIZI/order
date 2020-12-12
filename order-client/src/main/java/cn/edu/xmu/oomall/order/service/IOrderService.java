package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.OrderDTO;
import cn.edu.xmu.oomall.order.model.OrderInnerDTO;

import java.util.List;
import java.util.Map;

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

    ReturnObject<OrderInnerDTO> findOrderIdbyOrderItemId(Long orderItemId);

    ReturnObject<OrderDTO> getUserSelectSOrderInfo(Long userId, Long orderItemId);

    /**
     * 根据userId查询该用户的订单详情表，并根据skuId的list筛选orderItemId，返回orderItemId的List
     */
    ReturnObject<List<Long>> listUserSelectOrderItemIdBySkuList(Long userId, List<Long> skuId);

    /**
     * 根据shopId查询该商铺的所有订单详情表，并根据skuId的list筛选orderItemId，返回orderItemId的List
     */
    ReturnObject<List<Long>> listAdminSelectOrderItemIdBySkuList(Long shopId, List<Long> skuId);

    ReturnObject<Boolean> isOrderBelongToShop(Long shopId, Long orderId);

    ReturnObject<ResponseCode> getAdminHandleExchange(Long userId, Long shopId, Long orderItemId, Integer quantity, Long aftersaleId);

    /**
     * 支付完拆单
     * @param orderId
     * @return
     */
    ReturnObject<ResponseCode> splitOrders(Long orderId);


    /**
     * 根据orderItemId查询订单详情表和订单表信息，同时验证该orderItem是否属于该商店
     * shopId为0时表示管理员 无需验证
     */
    ReturnObject<OrderDTO> getShopSelectOrderInfo(Long shopId, Long orderItemId);

/**
 * 根据orderItemIdList查询订单详情表和订单表信息，同时验证该orderItem是否属于该店铺，返回orderItemId为key的Map
 * shopId为0时表示管理员 无需验证
 */
    ReturnObject<Map<Long,OrderDTO>> getShopSelectOrderInfoByList(Long shopId, List<Long> orderItemIdList);

/**
 * 根据orderItemIdList查询订单详情表和订单表信息，同时验证该orderItem是否属于该用户，返回orderItemId为key的Map
 */
//    ReturnObject<Map<Long,OrderDTO>> getUserSelectOrderInfoByList(Long userId, List<Long> orderItemIdList);
}
