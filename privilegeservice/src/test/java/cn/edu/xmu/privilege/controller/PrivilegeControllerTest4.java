package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * @author Xianwei Wang
 */
@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PrivilegeControllerTest4 {

    @Autowired
    private MockMvc mvc;

    private static final Logger logger = LoggerFactory.getLogger(PrivilegeControllerTest4.class);
    /**
     * 创建测试用token
     *
     * @author Xianwei Wang
     * @param userId
     * @param departId
     * @param expireTime
     * @return token
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }



    /**
     * 取消用户角色测试
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void revokeRoleTest() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(delete("/privilege/adminusersrole/90").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }
    /**
     * 赋予用户角色测试
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void assignRoleTest() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(post("/privilege/adminusers/47/roles/84").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":91,\"user\":{\"id\":47,\"userName\":\"2721900002\"},\"role\":{\"id\":84,\"name\":\"文案\",\"creatorId\":1,\"desc\":null,\"gmtCreate\":\"2020-11-01T09:48:24\",\"gmtModified\":\"2020-11-01T09:48:24\"},\"creator\":{\"id\":47,\"userName\":\"2721900002\"},\"gmtCreate\":\"\"},\"errmsg\":\"成功\"}";
        //JSONAssert.assertEquals(expectedResponse, responseString, false);

    }
    /**
     * 查看用户的角色测试
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getUserRoleTest() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(get("/privilege/adminusers/47/roles").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"id\":78,\"user\":{\"id\":47,\"userName\":\"2721900002\"},\"role\":{\"id\":85,\"name\":\"总经办\",\"createdBy\":1,\"desc\":null,\"gmtCreate\":\"2020-11-01T09:48:24\",\"gmtModified\":\"2020-11-01T09:48:24\"},\"creator\":{\"id\":1,\"userName\":\"13088admin\"},\"gmtCreate\":\"2020-11-01T09:48:24\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 查看自己的角色测试
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getSelfUserRoleTest() throws Exception {
        String token = creatTestToken(47L, 0L, 100);
        String responseString = this.mvc.perform(get("/privilege/adminusers/self/roles").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"id\":78,\"user\":{\"id\":47,\"userName\":\"2721900002\"},\"role\":{\"id\":85,\"name\":\"总经办\",\"createdBy\":1,\"desc\":null,\"gmtCreate\":\"2020-11-01T09:48:24\",\"gmtModified\":\"2020-11-01T09:48:24\"},\"creator\":{\"id\":1,\"userName\":\"13088admin\"},\"gmtCreate\":\"2020-11-01T09:48:24\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

}
