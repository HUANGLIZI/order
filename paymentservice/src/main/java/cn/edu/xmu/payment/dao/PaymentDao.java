package cn.edu.xmu.payment.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.mapper.FreightModelPoMapper;
import cn.edu.xmu.payment.mapper.OrdersPoMapper;
import cn.edu.xmu.payment.mapper.PaymentPoMapper;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.po.OrdersPo;
import cn.edu.xmu.payment.model.po.OrdersPoExample;
import cn.edu.xmu.payment.model.po.PaymentPo;
import cn.edu.xmu.payment.model.po.PaymentPoExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PaymentDao {

    private static final Logger logger = LoggerFactory.getLogger(PaymentDao.class);

    @Autowired
    PaymentPoMapper paymentPoMapper;

    @Autowired
    OrdersPoMapper ordersPoMapper;

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
            //通过调用其它模块的售后服务获得售后单id
            payment.setAftersaleId((long) 0x75bcd15);

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
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
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
}
