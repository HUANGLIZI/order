package cn.edu.xmu.freight.controller;

import cn.edu.xmu.freight.FreightServiceApplication;
import cn.edu.xmu.freight.model.vo.FreightModelChangeVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import com.github.sardine.ant.command.Put;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Caixin
 * @date 2020-11-30 17:27
 */
@SpringBootTest(classes = FreightServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc    //配置模拟的MVC，这样可以不启动服务器测试
@Transactional
public class CaixinFreghtControllerTest {

    @Autowired
    private MockMvc mvc;

    private static final Logger logger = LoggerFactory.getLogger(FreightControllerTest2.class);

    /**
     * 创建测试用token
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }

    @Test
    public void ChangeFreightModel1()
    {
        String token = creatTestToken(1L, 1L, 1000);
        FreightModelChangeVo freightModelChangeVo = new FreightModelChangeVo();
        freightModelChangeVo.setName("testChangeModel");
        freightModelChangeVo.setUnit(100);
        String roleJson = JacksonUtil.toJson(freightModelChangeVo);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/freight/shops/{shopId}/freightmodels/{id}",1,1)
                    .contentType("application/json;charset=UTF-8").content(roleJson)
                    .header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("=======");
//        System.out.println(responseString);
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ChangeFreightModel2()
    {
        String token = creatTestToken(1L, 1L, 1000);
        FreightModelChangeVo freightModelChangeVo = new FreightModelChangeVo();
        freightModelChangeVo.setName("freightModel");
        freightModelChangeVo.setUnit(100);
        String roleJson = JacksonUtil.toJson(freightModelChangeVo);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/freight/shops/{shopId}/freightmodels/{id}",1,1)
                    .contentType("application/json;charset=UTF-8").content(roleJson)
                    .header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":802,\"errmsg\":\"运费模板名重复\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
