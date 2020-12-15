package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import cn.edu.xmu.privilege.model.vo.LoginVo;
import cn.edu.xmu.privilege.model.vo.PrivilegeVo;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author yuehao
 * @date Created in 2020/11/4
 * Modified in 2020/11/7
 **/
@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getUserPrivs() throws Exception{
        String token = this.login("13088admin", "123456");
        String responseString = this.mvc.perform(get("/privilege/shops/0/adminusers/1/privileges").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":[{\"id\":2,\"name\":\"查看任意用户信息\",\"url\":\"/adminusers/{id}\",\"requestType\":0,\"gmtCreate\":\"2020-11-01T09:52:20\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":3,\"name\":\"修改任意用户信息\",\"url\":\"/adminusers/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:53:03\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":4,\"name\":\"删除用户\",\"url\":\"/adminusers/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T09:53:36\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":5,\"name\":\"恢复用户\",\"url\":\"/adminusers/{id}/release\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:59:24\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":6,\"name\":\"禁止用户登录\",\"url\":\"/adminusers/{id}/forbid\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:02:32\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":7,\"name\":\"赋予用户角色\",\"url\":\"/adminusers/{id}/roles/{id}\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:02:35\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":8,\"name\":\"取消用户角色\",\"url\":\"/adminusers/{id}/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:03:16\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":9,\"name\":\"新增角色\",\"url\":\"/roles\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:04:09\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":10,\"name\":\"删除角色\",\"url\":\"/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:04:42\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":11,\"name\":\"修改角色信息\",\"url\":\"/roles/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:05:20\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":12,\"name\":\"给角色增加权限\",\"url\":\"/roles/{id}/privileges/{id}\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:06:03\",\"gmtModified\":\"2020-11-02T21:51:46\"},{\"id\":13,\"name\":\"取消角色权限\",\"url\":\"/roleprivileges/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:06:43\",\"gmtModified\":\"2020-11-03T21:30:31\"},{\"id\":14,\"name\":\"修改权限信息\",\"url\":\"/privileges/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:08:18\",\"gmtModified\":\"2020-11-02T21:51:46\"},{\"id\":15,\"name\":\"查看所有用户的角色\",\"url\":\"/adminusers/{id}/roles\",\"requestType\":0,\"gmtCreate\":\"2020-11-03T17:53:38\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":16,\"name\":\"查看所有代理\",\"url\":\"/proxies\",\"requestType\":0,\"gmtCreate\":\"2020-11-03T17:55:31\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":17,\"name\":\"禁止代理关系\",\"url\":\"/allproxies/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-03T17:57:45\",\"gmtModified\":\"2020-11-03T19:48:47\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    public void getUserPriv2() throws Exception{
        String responseString = this.mvc.perform(get("/privilege/shops/0/adminusers/1000/privileges"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    private String login(String userName, String password) throws Exception{
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);

        String requireJson = JacksonUtil.toJson(vo);
        String response = this.mvc.perform(post("/privilege/privileges/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andReturn().getResponse().getContentAsString();
        return  JacksonUtil.parseString(response, "data");

    }


}

