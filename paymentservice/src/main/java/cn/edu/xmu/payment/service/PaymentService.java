package cn.edu.xmu.payment.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.dao.PaymentDao;
import cn.edu.xmu.payment.model.bo.Refund;
import cn.edu.xmu.payment.model.po.RefundPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

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
    public ReturnObject<VoObject> getOrdersRefundsByOrderId(Long id, Long shopId){
        List<RefundPo> retObj = paymentDao.getOrdersRefundsByOrderId(id);//if(retObj.getOrderId())
        ReturnObject<VoObject> retRefund = null;
        for(int i=0;i<retObj.size();i++) {
            ReturnObject<RefundPo> retObj2=new ReturnObject<>(retObj.get(i));
            if (retObj2.getCode().equals(ResponseCode.OK)) {
                retRefund = new ReturnObject<>((VoObject) retObj2.getData());
            } else {
                retRefund = new ReturnObject(retObj2.getCode(), retObj2.getErrmsg());
            }
        }
        return retRefund;
    }

    @Transactional
    public ReturnObject<VoObject> getOrdersRefundsByAftersaleId(Long id, Long shopId){
        List<RefundPo> retObj = paymentDao.getOrdersRefundsByAftersaleId(id);//if(retObj.getOrderId())
        ReturnObject<VoObject> retRefund = null;
        for(int i=0;i<retObj.size();i++) {
            ReturnObject<RefundPo> retObj2=new ReturnObject<>(retObj.get(i));
            if (retObj2.getCode().equals(ResponseCode.OK)) {
                retRefund = new ReturnObject<>((VoObject) retObj2.getData());
            } else {
                retRefund = new ReturnObject(retObj2.getCode(), retObj2.getErrmsg());
            }
        }
        return retRefund;
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

}
