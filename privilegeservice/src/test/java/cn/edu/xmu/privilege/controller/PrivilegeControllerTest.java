package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.util.AES;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import cn.edu.xmu.privilege.mapper.UserPoMapper;
import cn.edu.xmu.privilege.model.bo.User;
import cn.edu.xmu.privilege.model.po.UserPo;
import cn.edu.xmu.privilege.model.vo.LoginVo;
import cn.edu.xmu.privilege.model.vo.PrivilegeVo;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Ming Qiu
 * @date Created in 2020/11/4 0:33
 **/
@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PrivilegeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getAllPriv() throws Exception{
        String responseString = this.mvc.perform(get("/privilege/privileges"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":[{\"id\":2,\"name\":\"查看任意用户信息\",\"url\":\"/adminusers/{id}\",\"requestType\":0,\"gmtCreate\":\"2020-11-01T09:52:20\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":3,\"name\":\"修改任意用户信息\",\"url\":\"/adminusers/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:53:03\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":4,\"name\":\"删除用户\",\"url\":\"/adminusers/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T09:53:36\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":5,\"name\":\"恢复用户\",\"url\":\"/adminusers/{id}/release\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:59:24\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":6,\"name\":\"禁止用户登录\",\"url\":\"/adminusers/{id}/forbid\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:02:32\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":7,\"name\":\"赋予用户角色\",\"url\":\"/adminusers/{id}/roles/{id}\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:02:35\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":8,\"name\":\"取消用户角色\",\"url\":\"/adminusers/{id}/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:03:16\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":9,\"name\":\"新增角色\",\"url\":\"/roles\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:04:09\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":10,\"name\":\"删除角色\",\"url\":\"/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:04:42\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":11,\"name\":\"修改角色信息\",\"url\":\"/roles/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:05:20\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":12,\"name\":\"给角色增加权限\",\"url\":\"/roles/{id}/privileges/{id}\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:06:03\",\"gmtModified\":\"2020-11-02T21:51:46\"},{\"id\":13,\"name\":\"取消角色权限\",\"url\":\"/roleprivileges/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:06:43\",\"gmtModified\":\"2020-11-03T21:30:31\"},{\"id\":14,\"name\":\"修改权限信息\",\"url\":\"/privileges/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:08:18\",\"gmtModified\":\"2020-11-02T21:51:46\"},{\"id\":15,\"name\":\"查看所有用户的角色\",\"url\":\"/adminusers/{id}/roles\",\"requestType\":0,\"gmtCreate\":\"2020-11-03T17:53:38\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":16,\"name\":\"查看所有代理\",\"url\":\"/proxies\",\"requestType\":0,\"gmtCreate\":\"2020-11-03T17:55:31\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":17,\"name\":\"禁止代理关系\",\"url\":\"/allproxies/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-03T17:57:45\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":18,\"name\":\"取消任意用户角色\",\"url\":\"/adminuserroles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-03T19:52:04\",\"gmtModified\":\"2020-11-03T19:56:43\"},{\"id\":19,\"name\":\"管理员设置用户代理关系\",\"url\":\"/ausers/{id}/busers/{id}:\",\"requestType\":1,\"gmtCreate\":\"2020-11-04T13:10:02\",\"gmtModified\":null}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

/*    @Test
    @Audit
    public void changePriv(@LoginUser Long userId, @Depart Long departId) throws Exception{
        PrivilegeVo vo = new PrivilegeVo();
        vo.setName("车市");
        String json = "{\"name\":\"车市\"}";

        String responseString = this.mvc.perform(put("/privilege/privileges/2").contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals("", responseString, true);

        responseString = this.mvc.perform(get("/privilege/privileges"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":[{\"id\":2,\"name\":\"车市\",\"url\":\"/adminusers/{id}\",\"requestType\":0,\"gmtCreate\":\"2020-11-01T09:52:20\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":3,\"name\":\"修改任意用户信息\",\"url\":\"/adminusers/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:53:03\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":4,\"name\":\"删除用户\",\"url\":\"/adminusers/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T09:53:36\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":5,\"name\":\"恢复用户\",\"url\":\"/adminusers/{id}/release\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:59:24\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":6,\"name\":\"禁止用户登录\",\"url\":\"/adminusers/{id}/forbid\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:02:32\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":7,\"name\":\"赋予用户角色\",\"url\":\"/adminusers/{id}/roles/{id}\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:02:35\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":8,\"name\":\"取消用户角色\",\"url\":\"/adminusers/{id}/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:03:16\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":9,\"name\":\"新增角色\",\"url\":\"/roles\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:04:09\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":10,\"name\":\"删除角色\",\"url\":\"/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:04:42\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":11,\"name\":\"修改角色信息\",\"url\":\"/roles/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:05:20\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":12,\"name\":\"给角色增加权限\",\"url\":\"/roles/{id}/privileges/{id}\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:06:03\",\"gmtModified\":\"2020-11-02T21:51:46\"},{\"id\":13,\"name\":\"取消角色权限\",\"url\":\"/roleprivileges/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:06:43\",\"gmtModified\":\"2020-11-03T21:30:31\"},{\"id\":14,\"name\":\"修改权限信息\",\"url\":\"/privileges/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:08:18\",\"gmtModified\":\"2020-11-02T21:51:46\"},{\"id\":15,\"name\":\"查看所有用户的角色\",\"url\":\"/adminusers/{id}/roles\",\"requestType\":0,\"gmtCreate\":\"2020-11-03T17:53:38\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":16,\"name\":\"查看所有代理\",\"url\":\"/proxies\",\"requestType\":0,\"gmtCreate\":\"2020-11-03T17:55:31\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":17,\"name\":\"禁止代理关系\",\"url\":\"/allproxies/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-03T17:57:45\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":18,\"name\":\"取消任意用户角色\",\"url\":\"/adminuserroles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-03T19:52:04\",\"gmtModified\":\"2020-11-03T19:56:43\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }*/

    private String login(String userName, String password) throws Exception{
        LoginVo loginVo = new LoginVo();
        loginVo.setUserName(userName);
        loginVo.setPassword(password);
        String jsonString = JacksonUtil.toJson(loginVo);
        String responseString = this.mvc.perform(post("/privilege/adminusers/login").contentType("application/json;charset=UTF-8").content(jsonString))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        return responseString;
    }
}
