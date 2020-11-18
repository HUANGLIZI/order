package cn.edu.xmu.log.controller;


import cn.edu.xmu.log.LogServiceApplication;
import cn.edu.xmu.log.model.vo.LogVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Li Di Han
 * @date Created in 2020/11/18 10:33
 **/
@SpringBootTest(classes = LogServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
public class LogControllerTest {

    @Autowired
    private MockMvc mvc;

    /**
     * 创建测试用token
     *
     * @param userId
     * @param departId
     * @param expireTime
     * @return token
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        log.debug(token);
        return token;
    }


    /* log003： 清理日志 测试用例开始 */

    /**
     * 清空日志
     *
     * @throws Exception
     */
    @Test
    public void deleteLogs() throws Exception {
        LogVo logVo = new LogVo();
        logVo.setBeginTime("2020-10-10 00:00:00");
        logVo.setEndTime("2020-10-11 00:00:00");
        String token = creatTestToken(1L, 0L, 100);
        String responseString = null;
        try {
            responseString = this.mvc.perform(delete("/logs").header("authorization", token).contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(logVo)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String expectedResponse = "{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /* log003： 清理日志 测试用例结束 */
}
