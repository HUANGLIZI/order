package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
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

    /**
     * 取消用户角色测试
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void revokeRoleTest() throws Exception {
        String responseString = this.mvc.perform(delete("/privilege/adminusersrole/90"))
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

        String responseString = this.mvc.perform(post("/privilege/adminusers/47/roles/84?createid=47"))
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
        String responseString = this.mvc.perform(get("/privilege/adminusers/47/roles"))
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

        String responseString = this.mvc.perform(get("/privilege/adminusers/self/roles?id=47"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"id\":78,\"user\":{\"id\":47,\"userName\":\"2721900002\"},\"role\":{\"id\":85,\"name\":\"总经办\",\"createdBy\":1,\"desc\":null,\"gmtCreate\":\"2020-11-01T09:48:24\",\"gmtModified\":\"2020-11-01T09:48:24\"},\"creator\":{\"id\":1,\"userName\":\"13088admin\"},\"gmtCreate\":\"2020-11-01T09:48:24\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

}
