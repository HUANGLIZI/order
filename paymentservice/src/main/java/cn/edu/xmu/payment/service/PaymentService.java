package cn.edu.xmu.payment.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.OrderInnerDTO;
import cn.edu.xmu.oomall.order.service.IOrderService;
import cn.edu.xmu.oomall.order.service.IPaymentService;
import cn.edu.xmu.oomall.other.service.IAftersaleService;
import cn.edu.xmu.payment.dao.PaymentDao;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.bo.Refund;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@DubboService
public class PaymentService implements IPaymentService {

    @Autowired
    private PaymentDao paymentDao;

    @DubboReference
    private IOrderService iOrderService;

    @DubboReference
    private IAftersaleService iAftersaleService;

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

        //如果该商店不拥有这个order则查不到
        ReturnObject returnObject=iOrderService.isOrderBelongToShop(shopId,orderId);
        if(returnObject.getCode()!=ResponseCode.OK){
            logger.error(" queryPaymentById: 数据库不存在该支付单 orderId="+orderId);
            return returnObject;
        }
        return paymentDao.queryPayment(orderId);
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
        ReturnObject<OrderInnerDTO> orderInnerDTO = iOrderService.findShopIdbyOrderId(refund.getOrderId());
        Long retShopId = orderInnerDTO.getData().getShopId();
        ReturnObject<VoObject> retReund = null;
        if(retShopId==shopId) {
            ReturnObject<Refund> retObj = paymentDao.insertRefunds(refund);
            if (retObj.getCode().equals(ResponseCode.OK)) {
                retReund = new ReturnObject<>(retObj.getData());
            } else {
                retReund = new ReturnObject<VoObject>(retObj.getCode(), retObj.getErrmsg());
            }
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
        payment.setPaySn(Common.genSeqNum());
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
    public ReturnObject<ResponseCode> getAdminHandleRefund(Long userId, Long shopId, Long orderItemId, Long refund, Long aftersaleId)
    {
        ReturnObject<OrderInnerDTO> ret = iOrderService.findOrderIdbyOrderItemId(orderItemId);
        if (!ret.getCode().equals(ResponseCode.OK))
        {
            logger.info("orderInnerDTO is: " + ret.getData().toString());
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        OrderInnerDTO orderInnerDTO = ret.getData();
        if (!orderInnerDTO.getCustomerId().equals(userId) || !orderInnerDTO.getShopId().equals(shopId))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        ReturnObject<Long> paymentIdRet = paymentDao.getPaymentIdByOrderId(orderInnerDTO.getOrderId());
        if (!paymentIdRet.getCode().equals(ResponseCode.OK))
        {
            logger.info("paymentId is: " + paymentIdRet.getData().toString());
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        Refund refundBo = new Refund();
        refundBo.setAftersaleId(aftersaleId);
        refundBo.setAmount(Math.abs(refund));
        refundBo.setOrderId(orderInnerDTO.getOrderId());
        refundBo.setPaymentId(paymentIdRet.getData());
        Refund.State state = Refund.State.TO_BE_REFUND;
        refundBo.setState(state.getCode().byteValue());

        logger.info( "refundBo is: " + refundBo.toString());
        return paymentDao.createRefund(refundBo);
    }

    /**
     * 维修api，产生orderItemId的新支付单，refund为付款数，正数
     * @auther 洪晓杰
     * @return
     */
    @Override
    public ReturnObject<ResponseCode> getAdminHandleRepair(Long userId, Long shopId,Long orderItemId,Long refund,Long aftersaleId){

        logger.info("orderItemId: "+ orderItemId);
        //跨域获取orderId
        Long orderId=iOrderService.getOrderIdByOrderItemId(orderItemId).getData();
        logger.info("orderId: "+ orderId);

        Payment payment=new Payment();

        payment.setOrderId(orderId);
        payment.setAmount(refund);
        payment.setActualAmount(refund);
        payment.setPayTime(LocalDateTime.now());
        payment.setState((byte) 0);
        payment.setAftersaleId(aftersaleId);
        payment.setGmtCreate(LocalDateTime.now());

        return paymentDao.insertPayment(payment);
    }




    /**
     * 买家为售后单创建支付单
     *
     * @author 24320182203196 洪晓杰
     * @param payment 支付单，aftersaleId
     * @return Object 返回视图对象
     */
    @Transactional
    public ReturnObject<VoObject> createPaymentByAftersaleId(Payment payment,Long aftersaleId) {

        Long retOrderItemId = iAftersaleService.findOrderItemIdbyAftersaleId(aftersaleId).getData();

        if (retOrderItemId == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //跨域获取orderId
        Long retorderId=iOrderService.getOrderIdByOrderItemId(retOrderItemId).getData();


        //Long retOrderID=iOrderItemService

//        OrderItemPo orderItemPo=orderItemPoMapper.selectByPrimaryKey(retOrderItemId);
//        payment.setOrderId(orderItemPo.getOrderId());

        LocalDateTime localDateTime = LocalDateTime.now();

        payment.setAftersaleId(aftersaleId);
        payment.setOrderId(retorderId);
        payment.setBeginTime(LocalDateTime.now());
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

}
