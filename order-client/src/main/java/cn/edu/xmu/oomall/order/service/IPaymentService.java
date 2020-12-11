package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;

/**
 * @author Caixin
 * @date 2020-12-11 10:22
 */
public interface IPaymentService {
    ReturnObject<ResponseCode> getAdminHandleExchange(Long userId, Long shopId, Long orderItemId, Long refund, Long aftersaleId);
}
