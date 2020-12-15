package cn.edu.xmu.payment.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.OrderInnerDTO;
import cn.edu.xmu.oomall.order.service.IOrderService;
import cn.edu.xmu.oomall.other.service.IAftersaleService;
import cn.edu.xmu.payment.dao.PaymentDao;
import cn.edu.xmu.payment.model.bo.RefundBo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentServiceI {

    @Autowired
    private PaymentDao paymentDao;

    @DubboReference
    private IOrderService iOrderService;

    @DubboReference
    private IAftersaleService iAftersaleService;

    private Logger logger = LoggerFactory.getLogger(PaymentServiceI.class);

    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-03 20:06
     */
    @Transactional
    public ReturnObject<VoObject> userQueryRefundsByOrderId(Long orderId, Long userId)
    {
        ReturnObject<OrderInnerDTO> orderInnerDTO = iOrderService.findUserIdbyOrderId(orderId);
        if (!orderInnerDTO.getCode().equals(ResponseCode.OK))
        {
            logger.info("not found orderInnerDTO, the orderId is " + orderId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Long retUserId = orderInnerDTO.getData().getCustomerId();
        if (retUserId == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//        if (!retUserId.equals(1L))
        if (!retUserId.equals(userId))
        {
            logger.info("userId not fitted: the retUserId is: " + retUserId + " and the login user is: " + userId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }

        ReturnObject<VoObject> retRefund;
        ReturnObject<List<RefundBo>> returnObject = paymentDao.findRefundsInfoByOrderId(orderId);
        if (returnObject.getCode().equals(ResponseCode.OK)) {
            if (returnObject.getData().size() == 1) {
                retRefund = new ReturnObject<>(returnObject.getData().get(0));
            }else{
                logger.info("more than one same orderId: " + returnObject.getData().get(0).getOrderId());
                retRefund = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
        }else{
            retRefund = new ReturnObject<>(returnObject.getCode(), returnObject.getErrmsg());
        }
        return retRefund;
    }

    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-05 17:59
     */
    @Transactional
    public ReturnObject<VoObject> userQueryRefundsByAftersaleId(Long aftersaleId, Long userId)
    {
//        System.out.println(userId);
        ReturnObject<Long> ret = iAftersaleService.findUserIdbyAftersaleId(aftersaleId);
        if (!ret.getCode().equals(ResponseCode.OK))
        {
            logger.info("retUserId is " + ret.getData());
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Long retUserId = ret.getData();
//        System.out.println(userId+" "+retUserId);
//        if (!retUserId.equals(1L))
        if (retUserId == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        if (!retUserId.equals(userId))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

        ReturnObject<VoObject> retRefund;
        ReturnObject<List<RefundBo>> returnObject = paymentDao.findRefundsInfoByAfterSaleId(aftersaleId);
        if (returnObject.getCode().equals(ResponseCode.OK)) {
            logger.info("RefundBo list is: " + returnObject.getData().size());
            logger.info("aftersaleId is: " + aftersaleId);
            if (returnObject.getData().size() == 1) {
                retRefund = new ReturnObject<>(returnObject.getData().get(0));
            }else{
                retRefund = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
        }else{
            retRefund = new ReturnObject<>(returnObject.getCode(), returnObject.getErrmsg());
        }
        return retRefund;
    }


    @Transactional
    public ReturnObject<List<RefundBo>> getOrdersRefundsByAftersaleId(Long aftersaleId, Long shopId){
        ReturnObject<Long> ret = iAftersaleService.findShopIdbyAftersaleId(aftersaleId);
        if (!ret.getCode().equals(ResponseCode.OK))
        {
            logger.info("the return object is error!");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Long retShopId = ret.getData();
        if (retShopId == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        if (!retShopId.equals(shopId))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        ReturnObject<List<RefundBo>> retObj = paymentDao.getOrdersRefundsByAftersaleId(aftersaleId);//if(retObj.getOrderId())
        ReturnObject<List<RefundBo>> retRefund = null;
        List<Object> refundVoList = new ArrayList<>(retObj.getData().size());
        for (RefundBo bo: retObj.getData())
        {
            Object vo = bo.createVo();
            refundVoList.add(vo);
        }
        if (retObj.getCode().equals(ResponseCode.OK)) {
                retRefund = new ReturnObject(refundVoList);
            } else {
                retRefund = new ReturnObject(retObj.getCode(), retObj.getErrmsg());
            }
        return retRefund;
    }


    @Transactional
    public ReturnObject<List<Object>> getOrdersRefundsByOrderId(Long orderId, Long shopId){
        ReturnObject<OrderInnerDTO> orderInnerDTO = iOrderService.findShopIdbyOrderId(orderId);
        if (!orderInnerDTO.getCode().equals(ResponseCode.OK))
        {
            logger.info("not found orderInnerDTO, the orderId is " + orderId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Long retShopId = orderInnerDTO.getData().getShopId();
        if (retShopId == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        if (!retShopId.equals(shopId))
        {
            logger.info("shopId not fitted");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        ReturnObject<List<RefundBo>> retObj = paymentDao.getOrdersRefundsByOrderId(orderId);//if(retObj.getOrderId())
        ReturnObject<List<Object>> retRefund = null;
        List<Object> refundVoList = new ArrayList<>(retObj.getData().size());
        for (RefundBo bo: retObj.getData())
        {
            Object vo = bo.createVo();
            refundVoList.add(vo);
        }
        if (retObj.getCode().equals(ResponseCode.OK)) {
            retRefund = new ReturnObject<>(refundVoList);
        } else {
            retRefund = new ReturnObject(retObj.getCode(), retObj.getErrmsg());
        }
        return retRefund;
    }
}
