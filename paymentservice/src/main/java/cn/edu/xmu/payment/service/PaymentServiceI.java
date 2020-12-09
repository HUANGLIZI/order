package cn.edu.xmu.payment.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.OrderDTO;
import cn.edu.xmu.oomall.order.service.IOrderService;
import cn.edu.xmu.oomall.other.model.AftersaleDTO;
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
        ReturnObject<OrderDTO> orderDTO = iOrderService.findUserIdbyOrderId(orderId);
        Long retUserId = orderDTO.getData().getCustomerId();
        if (retUserId == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//        if (!retUserId.equals(1L))
        if (!retUserId.equals(userId))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        ReturnObject<VoObject> retRefund;
        ReturnObject<List<RefundBo>> returnObject = paymentDao.findRefundsInfoByOrderId(orderId);
        if (returnObject.getCode().equals(ResponseCode.OK)) {
            if (returnObject.getData().size() == 1) {
                retRefund = new ReturnObject<>(returnObject.getData().get(0));
            }else{
                retRefund = new ReturnObject<>();
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
        Long retUserId = iAftersaleService.findUserIdbyAftersaleId(aftersaleId).getData();
//        if (!retUserId.equals(1L))
//        Long retUserId = aftersaleDTO.getData().getCustomerId();
        if (retUserId == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        if (!retUserId.equals(userId))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        ReturnObject<VoObject> retRefund;
        ReturnObject<List<RefundBo>> returnObject = paymentDao.findRefundsInfoByAfterSaleId(aftersaleId);
        if (returnObject.getCode().equals(ResponseCode.OK)) {
            if (returnObject.getData().size() == 1) {
                retRefund = new ReturnObject<>(returnObject.getData().get(0));
            }else{
                retRefund = new ReturnObject<>();
            }
        }else{
            retRefund = new ReturnObject<>(returnObject.getCode(), returnObject.getErrmsg());
        }
        return retRefund;
    }


    @Transactional
    public ReturnObject<List<Object>> getOrdersRefundsByAftersaleId(Long aftersaleId, Long shopId){
        Long retShopId = iAftersaleService.findShopIdbyAftersaleId(aftersaleId).getData();
//        if (aftersaleDTO == null)
//            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//        Long retShopId = aftersaleDTO.getData().getShopId();
        if (retShopId == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        if (!retShopId.equals(shopId))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        ReturnObject<List<RefundBo>> retObj = paymentDao.getOrdersRefundsByAftersaleId(aftersaleId);//if(retObj.getOrderId())
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
//        }
        return retRefund;
    }


    @Transactional
    public ReturnObject<List<Object>> getOrdersRefundsByOrderId(Long id, Long shopId){
        ReturnObject<OrderDTO> orderDTO = iOrderService.findShopIdbyOrderId(id);
        Long retShopId = orderDTO.getData().getShopId();
        if (retShopId == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        if (!retShopId.equals(shopId))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        ReturnObject<List<RefundBo>> retObj = paymentDao.getOrdersRefundsByOrderId(id);//if(retObj.getOrderId())
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
