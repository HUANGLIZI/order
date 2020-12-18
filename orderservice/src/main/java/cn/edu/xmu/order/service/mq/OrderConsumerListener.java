//package cn.edu.xmu.order.service.mq;
//
//
//import cn.edu.xmu.ooad.util.JacksonUtil;
//import org.apache.rocketmq.spring.annotation.ConsumeMode;
//import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
//import org.apache.rocketmq.spring.core.RocketMQListener;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
///**
// * 消息消费者
// * @author Ming Qiu
// * @date Created in 2020/11/7 22:47
// **/
//@Service
//@RocketMQMessageListener(topic = "order-pay-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumeThreadMax = 10, consumerGroup = "pay-group")
//public class OrderConsumerListener implements RocketMQListener<String> {
//    private static final Logger logger = LoggerFactory.getLogger(OrderConsumerListener.class);
//    @Override
//    public void onMessage(String message) {
//        Integer orderId = JacksonUtil.toObj(message, Integer.class);
//        //加数据库
//        //ReturnObject<Orders> orders = orderDao.createOrders(ordersBo, orderItemsList);
//        logger.info("onMessage: got message orderId =" + orderId +" time = "+ LocalDateTime.now());
//    }
//}
