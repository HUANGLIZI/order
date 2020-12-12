package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;

/**
 * @author Caixin
 * @date 2020-12-11 10:22
 */
public interface IPaymentService {
    ReturnObject<ResponseCode> getAdminHandleRefund(Long userId, Long shopId, Long orderItemId, Long refund, Long aftersaleId);

    /**
     * 维修api，产生orderItemId的新支付单，refund为付款数，正数
     * @author 洪晓杰
     */
    ReturnObject<ResponseCode> getAdminHandleRepair(Long userId, Long shopId, Long orderItemId, Long refund,Long aftersaleId);



}
