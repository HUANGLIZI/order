package cn.edu.xmu.log.service;

import cn.edu.xmu.log.LogServiceApplication;
import cn.edu.xmu.log.model.bo.Log;
import cn.edu.xmu.ooad.util.JacksonUtil;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author Fiber W.
 * created at 11/18/20 10:10 AM
 * @detail cn.edu.xmu.log.service
 */
@SpringBootTest(classes = LogServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RocketMQServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(RocketMQService.class);

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmqdemo.order-pay-topic.delay-level}")
    private int delayLevel;

    @Value("${rocketmqdemo.order-pay-topic.timeout}")
    private long timeout;


    @Test
    public void sendLogMessageTest(){
        Log log = new Log();
        log.setIp("127.0.0.1");
        log.setDescr("test");
        log.setGmtCreate(LocalDateTime.now());
        log.setPrivilegeId(Long.valueOf(1));

        String json = JacksonUtil.toJson(log);
        Message message = MessageBuilder.withPayload(json).build();
        logger.info("sendLogMessage: message = " + message);
        rocketMQTemplate.sendOneWay("log-topic:1", message);
    }
}
