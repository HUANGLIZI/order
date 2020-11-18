package cn.edu.xmu.log.service;

import cn.edu.xmu.log.model.bo.Log;
import cn.edu.xmu.ooad.util.JacksonUtil;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @description RocketMQService
 * @author Xianwei Wang
 * created at 11/18/20 9:48 AM
 */
@Service
public class RocketMQService {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQService.class);

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * @description 生产者发送消息
     * @param log 日志
     * @return void
     * @author Xianwei Wang
     * created at 11/18/20 11:57 AM
     */
    @Transactional
    public void sendLogMessage(Log log){

        String json = JacksonUtil.toJson(log);
        Message message = MessageBuilder.withPayload(json).build();
        logger.info("sendLogMessage: message = " + message);
        rocketMQTemplate.sendOneWay("log-topic:1", message);
    }

}
