package cn.edu.xmu.payment.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.OrderDTO;
import cn.edu.xmu.oomall.order.model.OrderInnerDTO;
import cn.edu.xmu.oomall.order.service.IOrderService;
import cn.edu.xmu.oomall.order.service.IPaymentService;
import cn.edu.xmu.oomall.other.service.IAftersaleService;
import cn.edu.xmu.payment.dao.PaymentDao;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.bo.Refund;
import cn.edu.xmu.payment.model.po.PaymentPo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    public ReturnObject userQueryPayment(Long orderId, Long userId) {
        ReturnObject<OrderInnerDTO> returnObject = iOrderService.findUserIdbyOrderId(orderId);
        if(returnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
            return returnObject;
        else if(!userId.equals(returnObject.getData().getCustomerId()))
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
        return paymentDao.userQueryPaymentById(orderId);
    }

    public ReturnObject queryPayment(Long shopId, Long orderId) {
        //如果该商店不拥有这个order则查不到
        ReturnObject<ResponseCode> returnObject = iOrderService.judgeOrderBelongToShop(shopId, orderId);
        if(returnObject.getCode().equals(ResponseCode.OK)){
            return paymentDao.queryPayment(orderId);
        }
        else if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE)){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        else if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)){
            logger.error(" queryPaymentById: 数据库不存在该支付单 orderId="+orderId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject<>();
    }



    /**
     * 买家查询自己的支付信息
     *
     * @author 24320182203196 洪晓杰
     * @return ReturnObject 查询结果
     */
    public ReturnObject customerQueryPaymentByAftersaleId(Long aftersaleId,Long userId) {
        ReturnObject<Long> returnObject = iAftersaleService.findUserIdbyAftersaleId(aftersaleId);
        if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST))
        {
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if(!returnObject.getData().equals(userId))
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
        return paymentDao.queryPaymentByAftersaleIdForCus(aftersaleId);
    }

    /**
     * 买家查询自己的支付信息
     *
     * @author 24320182203196 洪晓杰
     * @return ReturnObject 查询结果
     */
    public ReturnObject<List> getPaymentByAftersaleId(Long shopId, Long aftersaleId) throws Exception {

        List<Payment> payments=null;
        List<PaymentPo> retPaymentPos=paymentDao.getOrderIdFromPaymentByAftersaleId(aftersaleId);

        logger.error("1");

        //如果存在找不到对应记录的时候
        if(retPaymentPos.isEmpty())return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("不存在对应的aftersaleId的记录" ));

        logger.error("1");

        payments=new ArrayList<>(retPaymentPos.size());


        logger.error("1");

        Long retShopId=null;
        for(PaymentPo paymentPo:retPaymentPos){
            try {
                retShopId=iOrderService.findShopIdbyOrderId(paymentPo.getOrderId()).getData().getShopId();
            }catch (Exception e){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("不存在的id" ));
            }
            //有可能存在找不到的情况
            if(retShopId.equals(shopId)) payments.add(new Payment(paymentPo));
            //if(retShopId.equals(null))return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("找不到orderId对应的shopId" ));
        }

        if(payments.isEmpty())return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("找不到符合条件的支付记录" ));

        return new ReturnObject<>(payments);
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
    public ReturnObject<ResponseCode> getAdminHandleRefund(Long userId, Long shopId, Long orderItemId, Long refund, Long aftersaleId,Integer quantity)
    {
        ReturnObject<OrderInnerDTO> ret = iOrderService.findOrderIdbyOrderItemId(orderItemId);
        if (!ret.getCode().equals(ResponseCode.OK))
        {
            logger.info("orderInnerDTO is: " + ret.getData().toString());
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        OrderInnerDTO orderInnerDTO = ret.getData();
//        ReturnObject<ResponseCode> returnObject = iOrderService.judgeOrderFinished(orderInnerDTO.getOrderId());
//        if (!returnObject.getCode().equals(ResponseCode.OK))
//            return new ReturnObject<>(returnObject.getCode());
        if (!orderInnerDTO.getCustomerId().equals(userId) || !orderInnerDTO.getShopId().equals(shopId))
        {
            logger.info("out!!!");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        ReturnObject<Long> paymentIdRet = paymentDao.getPaymentIdByOrderId(orderInnerDTO.getOrderId());
        if (!paymentIdRet.getCode().equals(ResponseCode.OK))
        {
            logger.info("paymentId is: " + paymentIdRet.getData().toString());
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        Refund refundBo = new Refund();
        refundBo.setAftersaleId(aftersaleId);
        if (refund != null)
            refundBo.setAmount(Math.abs(refund));
        else
        {
            ReturnObject<Long> returnObject = iOrderService.getDiscountPriceByOrderItemId(orderItemId);
            if (!returnObject.getCode().equals(ResponseCode.OK)) {
                logger.info("out2!!");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            Long discountPrice = returnObject.getData();
            refundBo.setAmount(discountPrice*quantity);
        }
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

//        ReturnObject<ResponseCode> returnObject = iOrderService.judgeOrderFinished(orderId);
//        if (!returnObject.getCode().equals(ResponseCode.OK))
//            return new ReturnObject<>(returnObject.getCode());

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

        //Long retorderId=(long)0;
        //？？？？？？？？？？？？？？？？？？？？？？？？？记得改回来
        logger.error("记得改回来");
        //跨域获取orderItemId
        Long retOrderItemId = iAftersaleService.findOrderItemIdbyAftersaleId(aftersaleId).getData();
        //
        //第一个测试
        //Long retOrderItemId=47007;
        //第二个测试
        // Long retOrderItemId=null;


        //Long retOrderItemId=null;
        if (retOrderItemId == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"找不到aftersaleId对应的OrderItemId");

        logger.error("111111111");

        ReturnObject<Long> returnObt=iOrderService.getOrderIdByOrderItemId(retOrderItemId);

        logger.error("22222222");

        System.out.println(returnObt.getCode());

        System.out.println(returnObt.getData());

        Long retorderId=returnObt.getData();
        System.out.println(retorderId);


        LocalDateTime localDateTime = LocalDateTime.now();

        payment.setAftersaleId(aftersaleId);
        payment.setOrderId(retorderId);
        payment.setBeginTime(LocalDateTime.now());
        payment.setEndTime(localDateTime.plusHours(24));
        //支付成功
        payment.setState((byte)0);
        payment.setPayTime(localDateTime);
        //payment.setGmtModified(localDateTime);

        ReturnObject returnObject = paymentDao.insertPayment(payment);
        if(returnObject.getCode().equals(ResponseCode.OK)){
            return returnObject;
        }else {
            return new ReturnObject<>(returnObject.getCode(),returnObject.getErrmsg());
        }
    }
    /**
     * 为对应的orderId查找payment并创建退款单。、
     * 实现IPaymentService.java中 “ReturnObject<ResponseCode> createRefundbyOrederId(Long orderId);”
     * @author 李明明
     * @param shopId, orderId
     * @return
     * @date 2020-12-14
     */
    @Override
    public ReturnObject<ResponseCode> createRefundbyOrederId(Long shopId, Long orderId)
    {
        //调用 "public ReturnObject queryPayment(Long shopId, Long orderId)"
        ReturnObject<List<Payment>> returnObject = paymentDao.queryPayment(orderId);
        List<Payment> paymentList = returnObject.getData();
        //paymentList大概会有1个或2个元素,
        // 1个元素:是支付定金产生的
        // 2个元素:一个是支付定金时产生的,另一个是支付尾款时产生的,分两次共两个退款单分别退款
        for(Payment payment : paymentList)
        {
            Refund refund = new Refund();
            refund.setPaymentId(payment.getId());
            refund.setOrderId(orderId);
            refund.setState(Refund.State.HAS_REFUND.getCode().byteValue());
            refund.setAmount(payment.getActualAmount());
            refund.setGmtCreate(LocalDateTime.now());
            ReturnObject<Refund> returnObject1 = paymentDao.insertRefunds(refund);
            if(returnObject1.getCode() != ResponseCode.OK) {
                return new ReturnObject<ResponseCode>(returnObject1.getCode(), returnObject1.getErrmsg());
            }
        }
        return new ReturnObject<>(ResponseCode.OK);
    }

    /**
     * 处理成团的情况,首先需要将支付单中的actual_amount换成减去团购优惠后的金额,然后为该支付单创建退款单
     * @param orderId
     * @param refundPrice
     * @param shopId
     * @return
     * @author 李明明
     * @date 2020-12-14
     */
    @Override
    public ReturnObject<ResponseCode> createRefundForGrouponByOrderId(Long orderId, Long shopId, Long refundPrice)
    {
        //System.out.println("*************欢迎进入paymentService***********");
        ReturnObject<List<Payment>> returnObject = paymentDao.queryPayment(orderId);
        if(returnObject.getCode() != ResponseCode.OK)
            return new ReturnObject<>(returnObject.getCode(),returnObject.getErrmsg());
        List<Payment> paymentList = returnObject.getData();
        for(Payment payment : paymentList)
        {
            ReturnObject<ResponseCode> returnObject1 = paymentDao.setActualAmount(payment.getId(),refundPrice);
            if(returnObject1.getCode() != ResponseCode.OK)
            {
                return new ReturnObject<>(returnObject1.getCode(),returnObject1.getErrmsg());
            }
            Refund refund = new Refund();
            refund.setPaymentId(payment.getId());
            refund.setOrderId(orderId);
            refund.setState(Refund.State.HAS_REFUND.getCode().byteValue());
            refund.setAmount(refundPrice);
            refund.setGmtCreate(LocalDateTime.now());
            ReturnObject<Refund> returnObject2 = paymentDao.insertRefunds(refund);
            if(returnObject1.getCode() != ResponseCode.OK) {
                return new ReturnObject<ResponseCode>(returnObject1.getCode(), returnObject1.getErrmsg());
            }
        }
        return new ReturnObject<>(ResponseCode.OK);
    }

}
