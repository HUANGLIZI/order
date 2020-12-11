package cn.edu.xmu.payment.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.OrderInnerDTO;
import cn.edu.xmu.oomall.order.service.IOrderService;
import cn.edu.xmu.oomall.order.service.IPaymentService;
import cn.edu.xmu.payment.dao.PaymentDao;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.bo.Refund;
import cn.edu.xmu.payment.model.po.RefundPo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService implements IPaymentService {

    @Autowired
    private PaymentDao paymentDao;

    @DubboReference
    private IOrderService iOrderService;

    private Logger logger = LoggerFactory.getLogger(PaymentService.class);

    /**
     * 买家查询自己的支付信息
     *
     * @author 24320182203327 张湘君
     * @param orderId 订单id
     * @return ReturnObject 查询结果
     * createdBy 张湘君 2020/12/1 20:12
     * modifiedBy 张湘君 2020/12/1 20:12
     */
    public ReturnObject userQueryPayment(Long orderId) {
        return paymentDao.userQueryPaymentById(orderId);
    }

    public ReturnObject queryPayment(Long shopId, Long orderId) {
        return paymentDao.queryPayment(shopId,orderId);
    }



    /**
     * 买家查询自己的支付信息
     *
     * @author 24320182203196 洪晓杰
     * @return ReturnObject 查询结果
     */
    public ReturnObject customerQueryPaymentByAftersaleId(Long aftersaleId) {
        return paymentDao.queryPaymentByAftersaleIdForCus(aftersaleId);
    }

    /**
     * 买家查询自己的支付信息
     *
     * @author 24320182203196 洪晓杰
     * @return ReturnObject 查询结果
     */
    public ReturnObject getPaymentByAftersaleId(Long shopId, Long aftersaleId) {

        return paymentDao.queryPaymentByAftersaleIdForAdmin(shopId,aftersaleId);

    }

    @Transactional
    public ReturnObject<VoObject> insertRefunds(Refund refund, Long shopId){
        ReturnObject<Refund> retObj = paymentDao.insertRefunds(refund);
        ReturnObject<VoObject> retReund = null;
        if (retObj.getCode().equals(ResponseCode.OK)) {
            retReund = new ReturnObject<>(retObj.getData());
        } else {
            retReund = new ReturnObject<VoObject>(retObj.getCode(), retObj.getErrmsg());
        }
        return retReund;
    }

    /**
     * 买家为订单创建支付单
     *
     * @author 24320182203327 张湘君
     * @param payment 支付单
     * @return Object 角色返回视图
     * createdBy 张湘君 2020/12/3 20:12
     * modifiedBy 张湘君 2020/12/3 20:12
     */
    public ReturnObject<VoObject> createPayment(Payment payment) {

        LocalDateTime localDateTime = LocalDateTime.now();

        payment.setBeginTime(localDateTime);
        payment.setEndTime(localDateTime.plusHours(24));
        //支付成功
        payment.setState((byte)0);
        payment.setPayTime(localDateTime);

        payment.setGmtModified(localDateTime);

        ReturnObject returnObject = paymentDao.insertPayment(payment);
        if(returnObject.getCode().equals(ResponseCode.OK)){
            return returnObject;
        }else {
            return new ReturnObject<>(returnObject.getCode(),returnObject.getErrmsg());
        }
    }

    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-11 10:20
     */
    // 通过 orderItemId 查找 orderId
    // 找 paymentId
    @Override
    public ReturnObject<ResponseCode> getAdminHandleExchange(Long userId, Long shopId, Long orderItemId, Long refund, Long aftersaleId)
    {
        OrderInnerDTO orderInnerDTO = iOrderService.findOrderIdbyOrderItemId(orderItemId).getData();
        if (!orderInnerDTO.getCustomerId().equals(userId) || !orderInnerDTO.getShopId().equals(shopId))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        Long paymentId = paymentDao.getPaymentIdByOrderId(orderInnerDTO.getOrderId()).getData();
        Refund refundBo = new Refund();
        refundBo.setAftersaleId(aftersaleId);
        refundBo.setAmount(Math.abs(refund));
        refundBo.setOrderId(orderInnerDTO.getOrderId());
        refundBo.setPaymentId(paymentId);
        Refund.State state = Refund.State.TO_BE_REFUND;
        refundBo.setState(state.getCode().byteValue());

        return paymentDao.createRefund(refundBo);
    }
}
