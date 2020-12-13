package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.ooad.util.ReturnObject;

public interface IOrderItemService {

    /**
     * 通过OrderItemId获取OrderId
     * @author 洪晓杰
     */
    ReturnObject<Long> getOrderIdByOrderItemId(Long orderId);
}
