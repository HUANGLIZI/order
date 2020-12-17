package cn.edu.xmu.oomall.order.service;


import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;

public interface IPaymentService {

    /**
     * 退款api，产生orderitemId的退款，refund为退款数，负数
     */
    ReturnObject<ResponseCode> getAdminHandleRefund(Long userId, Long shopId, Long orderItemId, Long refund, Long aftersaleId);
    
    /**
     * 维修api，产生orderitemId的新支付单，refund为付款数，正数
     */
    ReturnObject<ResponseCode> getAdminHandleRepair(Long userId, Long shopId, Long orderItemId, Long refund, Long aftersaleId);

    /**
     * OrderService.java中“public ReturnObject<Object> putPresaleOffshavles(Long presaleId)”方法调该改接口
     * 为对应的orderId查找payment并创建退款单。
     * 在PaymentService.java中实现
     * @param shopId, orderId
     * @return
     * @author 李明明
     * @date 2020-12-14
     */
    ReturnObject<ResponseCode> createRefundbyOrederId(Long shopId, Long orderId);

    /**
     * 处理成团的情况,首先需要将支付单中的actual_amount换成减去团购优惠后的金额,然后为该支付单创建退款单
     * OrderService.java中 "public ReturnObject<Object> grouponEnd(String strategy, Long GrouponId)"方法调用该接口
     * 在PaymentService.java中实现
     * @param orderId
     * @param refundPrice
     * @param shopId
     * @return
     * @author 李明明
     * @date 2020-12-14
     */
    ReturnObject<ResponseCode> createRefundForGrouponByOrderId(Long orderId, Long shopId, Long refundPrice);
}
