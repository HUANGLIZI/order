package cn.edu.xmu.payment.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.mapper.PaymentPoMapper;
import cn.edu.xmu.payment.mapper.RefundPoMapper;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.bo.Refund;
import cn.edu.xmu.payment.model.bo.RefundBo;
import cn.edu.xmu.payment.model.po.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class PaymentDao {

    private static final Logger logger = LoggerFactory.getLogger(PaymentDao.class);

    @Autowired
    PaymentPoMapper paymentPoMapper;

    @Autowired
    OrdersPoMapper ordersPoMapper;

    @Autowired
    RefundPoMapper refundPoMapper;

    /**
     * 买家查询自己的支付信息
     *
     * @author 24320182203327 张湘君
     * @param orderId 订单id
     * @return ReturnObject查询结果
     * createdBy 张湘君 2020/12/1 20:12
     * modifiedBy 张湘君 2020/12/1 20:12
     */
    public ReturnObject userQueryPaymentById(Long orderId) {
        PaymentPoExample example=new PaymentPoExample();
        PaymentPoExample.Criteria criteria=example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<PaymentPo> paymentPoS = paymentPoMapper.selectByExample(example);
        //po对象为空，没查到
        if (paymentPoS.size() == 0) {
            logger.error(" userQueryPaymentById: 数据库不存在该支付单 orderId="+orderId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        List<Payment> payments =new ArrayList<>(paymentPoS.size());
        for(PaymentPo paymentPo:paymentPoS){
            Payment payment = new Payment(paymentPo);
            payments.add(payment);
        }
        return new ReturnObject<>(payments);
    }

    public ReturnObject queryPayment(Long shopId, Long orderId) {
        PaymentPoExample example=new PaymentPoExample();
        PaymentPoExample.Criteria criteria=example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<PaymentPo> paymentPoS = paymentPoMapper.selectByExample(example);
        //po对象为空，没查到
        if (paymentPoS.size() == 0) {
            logger.error(" queryPaymentById: 数据库不存在该支付单 orderId="+orderId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        //如果该商店不拥有这个order则查不到
        if(!isOrderBelongToShop(shopId,orderId)){
            logger.error(" queryPaymentById: 数据库不存在该支付单 orderId="+orderId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }


        List<Payment> payments =new ArrayList<>(paymentPoS.size());
        for(PaymentPo paymentPo:paymentPoS){
            Payment payment = new Payment(paymentPo);
            //通过调用其它模块的售后服务获得售后单id
            payment.setAftersaleId((long) 0x75bcd15);

            payments.add(payment);
        }
        return new ReturnObject<>(payments);
    }

    private boolean isOrderBelongToShop(Long shopId, Long orderId){
        OrdersPoExample example=new OrdersPoExample ();
        OrdersPoExample.Criteria criteria=example.createCriteria();
        criteria.andIdEqualTo(orderId);
        criteria.andShopIdEqualTo(shopId);


        List<OrdersPo> ordersPos=ordersPoMapper.selectByExample(example);
        return !ordersPos.isEmpty();
    }


    /**
     * 买家通过aftersaleId查询自己的支付信息
     * @author  洪晓杰
     */
    public ReturnObject queryPaymentByAftersaleIdForCus(Long aftersaleId) {
        PaymentPoExample example=new PaymentPoExample();
        PaymentPoExample.Criteria criteria=example.createCriteria();
        criteria.andAftersaleIdEqualTo(aftersaleId);
        List<PaymentPo> paymentPoS = paymentPoMapper.selectByExample(example);
        //po对象为空，没查到
        if (paymentPoS.size() == 0) {
            logger.error(" queryPaymentByAftersaleId: 数据库不存在该支付单 aftersaleId="+aftersaleId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        List<Payment> payments =new ArrayList<>(paymentPoS.size());
        for(PaymentPo paymentPo:paymentPoS){
            Payment payment = new Payment(paymentPo);
            payments.add(payment);
        }
        return new ReturnObject<>(payments);
    }

    /**
     * 买家通过aftersaleId、shopId查询自己的支付信息
     * @author  洪晓杰
     */
    public ReturnObject queryPaymentByAftersaleIdForAdmin(Long shopId, Long aftersaleId) {
        PaymentPoExample example=new PaymentPoExample();
        PaymentPoExample.Criteria criteria=example.createCriteria();
        criteria.andOrderIdEqualTo(aftersaleId);
        List<PaymentPo> paymentPoS = paymentPoMapper.selectByExample(example);
        //po对象为空，没查到
        if (paymentPoS.size() == 0) {
            logger.error(" queryPaymentById: 数据库不存在该支付单 orderId="+aftersaleId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        List<Payment> payments =new ArrayList<>(paymentPoS.size());
        for(PaymentPo paymentPo:paymentPoS){
            Payment payment = new Payment(paymentPo);
            payments.add(payment);
        }
        return new ReturnObject<>(payments);
    }

    /**
     * 根据OrderId获取退款信息
     * @author Li Zihan 24320182203227
     * @param id
     * @return 用户
     */
    public ReturnObject<List<RefundBo>> getOrdersRefundsByOrderId(Long id) {
        logger.debug("findRefundByOrderId: Id =" + id);
        RefundPoExample refundPoExample=new RefundPoExample();
        RefundPoExample.Criteria criteria=refundPoExample.createCriteria();
        criteria.andOrderIdEqualTo(id);
        List<RefundPo> refundPos=refundPoMapper.selectByExample(refundPoExample);
        if (refundPos == null) {
            logger.error("getNewUser: 数据库不存在该Order的退款信息 orderId=" + id);
        }
        List<RefundBo> refundBoList = new ArrayList<>(refundPos.size());
        for (RefundPo po: refundPos)
        {
            RefundBo refundBo = new RefundBo(po);
            refundBoList.add(refundBo);
        }

        return new ReturnObject<>(refundBoList);
    }
    /**
     * 根据AftersaleId获取退款信息
     * @author Li Zihan 24320182203227
     * @param id
     * @return 用户
     */
    public ReturnObject<List<RefundBo>> getOrdersRefundsByAftersaleId(Long id) {
        logger.debug("findRefundByAftersaleId: Id =" + id);
        RefundPoExample refundPoExample=new RefundPoExample();
        RefundPoExample.Criteria criteria=refundPoExample.createCriteria();
        criteria.andAftersaleIdEqualTo(id);
        List<RefundPo> refundPos=refundPoMapper.selectByExample(refundPoExample);
        if (refundPos == null) {
            logger.error("getNewUser: 数据库不存在该Order的退款信息 orderId=" + id);
        }
        List<RefundBo> refundBoList = new ArrayList<>(refundPos.size());
        for (RefundPo po: refundPos)
        {
            RefundBo refundBo = new RefundBo(po);
            refundBoList.add(refundBo);
        }

        return new ReturnObject<>(refundBoList);
    }
    /**
     * 增加一个退款信息
     * @author 24320182203227 李子晗
     */
    public ReturnObject<Refund> insertRefunds(Refund refund) {
        RefundPo refundPo = refund.gotRefundPo();
        ReturnObject<Refund> retObj = null;
        try{
            int ret = refundPoMapper.insertSelective(refundPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertRefund: insert refund fail " + refundPo.toString());
//                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + refundPo.getPaySn()));
            } else {
                //插入成功
                logger.debug("insertRefund: insert refund = " + refundPo.toString());
                refund.setId(refundPo.getId());
                retObj = new ReturnObject<>(refund);
            }
        }
        catch (DataAccessException e) {
            if (Objects.requireNonNull(e.getMessage()).contains("refund.pay_sn_uindex")) {
                //若有重复的角色名则新增失败
                logger.debug("updateRole: have same PaySn = " + refundPo.toString());
                retObj = new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("支付号重复：" + refundPo.toString()));
            } else {
                // 其他数据库错误
                logger.debug("other sql exception : " + e.getMessage());
                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    public ReturnObject insertPayment(Payment payment) {
        PaymentPo paymentPo = payment.getPaymentPo();
        ReturnObject returnObject;
        try{
            int ret = paymentPoMapper.insertSelective(paymentPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertPayment: insert Payment fail " + paymentPo.toString());
                returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + paymentPo.toString()));
            } else {
                //插入成功
                logger.debug("insertPayment: insert Payment  = " + paymentPo.toString());
                payment.setId(paymentPo.getId());
                returnObject = new ReturnObject<>(payment);
            }
        }
        catch (DataAccessException e) {
            if (Objects.requireNonNull(e.getMessage()).contains("payment.pay_sn_uindex")) {
                //若有重复的流水号则新增失败
                logger.debug("updateRole: have same paySn = " + paymentPo.getPaySn());
                returnObject = new ReturnObject<>(ResponseCode.PAYSN_SAME, String.format("流水号重复：" + paymentPo.toString()));
            } else {
                // 其他数据库错误
                logger.debug("other sql exception : " + e.getMessage());
                returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            returnObject  = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return returnObject ;
    }


    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-06 22:53
     */
    public ReturnObject<List<RefundBo>> findRefundsInfoByOrderId(Long orderId)
    {
        RefundPoExample refundPoExample = new RefundPoExample();
        RefundPoExample.Criteria criteria = refundPoExample.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<RefundPo> refundPoList = refundPoMapper.selectByExample(refundPoExample);

        if (refundPoList.size() == 0)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        List<RefundBo> refundBoList = new ArrayList<>(refundPoList.size());
        for (RefundPo po: refundPoList)
        {
            RefundBo refundBo = new RefundBo(po);
            refundBoList.add(refundBo);
        }

        return new ReturnObject<>(refundBoList);
    }

    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-06 22:53
     */
    public ReturnObject<List<RefundBo>> findRefundsInfoByAfterSaleId(Long afterSaleId)
    {
        RefundPoExample refundPoExample = new RefundPoExample();
        RefundPoExample.Criteria criteria = refundPoExample.createCriteria();
        criteria.andOrderIdEqualTo(afterSaleId);
        List<RefundPo> refundPoList = refundPoMapper.selectByExample(refundPoExample);

        if (refundPoList.size() == 0)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        List<RefundBo> refundBoList = new ArrayList<>(refundPoList.size());
        for (RefundPo po: refundPoList)
        {
            RefundBo refundBo = new RefundBo(po);
            refundBoList.add(refundBo);
        }

        return new ReturnObject<>(refundBoList);
    }

}
