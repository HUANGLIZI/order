package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.util.AES;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import cn.edu.xmu.privilege.mapper.UserPoMapper;
import cn.edu.xmu.privilege.model.bo.User;
import cn.edu.xmu.privilege.model.po.UserPo;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Han Li
 * @date Created at 4/11/2020 23:55
 **/
@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminUserControllerTest {

    @Autowired
    private MockMvc mvc;

    /* auth009 测试用例 */

    @Autowired
    private UserPoMapper userPoMapper;

    /**
     * 测试更新用户资料
     * @throws Exception Assert 或 HTTP 错误
     */
    @Test
    public void modifyUserNoExceptions() throws Exception {
        String contentJson = "{\n" +
                "    \"name\": \"张小绿\",\n" +
                "    \"email\": \"han@han-li.cn\",\n" +
                "    \"mobile\": \"13906008040\"\n" +
                "}";

        String responseString = this.mvc.perform(
                put("/adminusers/59").contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试有关数据是否有真的改变
        UserPo updatedPo = userPoMapper.selectByPrimaryKey(59L);
        Assert.state(AES.decrypt(updatedPo.getName(), User.AESPASS).equals("张小绿"), "用户名不相等！");
        Assert.state(AES.decrypt(updatedPo.getEmail(), User.AESPASS).equals("han@han-li.cn"), "Email 不相等！");
        Assert.state(AES.decrypt(updatedPo.getMobile(), User.AESPASS).equals("13906008040"), "电讯号码不相等！");
    }

    /**
     * 测试更新用户资料 (Email 重复)
     * @throws Exception Assert 或 HTTP 错误
     */
    @Test
    public void modifyUserDuplicateEmail() throws Exception {
        String contentJson = "{\n" +
                "    \"name\": \"祝大米\",\n" +
                "    \"email\": \"han@han-li.cn\",\n" +
                "    \"mobile\": \"112452463123\"\n" +
                "}";

        this.mvc.perform(
                put("/adminusers/58").contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        contentJson = "{\n" +
                "    \"name\": \"叶小朵\",\n" +
                "    \"email\": \"han@han-li.cn\",\n" +
                "    \"mobile\": \"13807710771\"\n" +
                "}";

        String responseString = this.mvc.perform(
                put("/adminusers/55").contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":732,\"errmsg\":\"邮箱已被注册\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试更新用户资料 (电话重复)
     * @throws Exception Assert 或 HTTP 错误
     */
    @Test
    public void modifyUserDuplicateTel() throws Exception {
        String contentJson = "{\n" +
                "    \"name\": \"猪小花\",\n" +
                "    \"email\": \"jajaja@han-li.cn\",\n" +
                "    \"mobile\": \"13906008040\"\n" +
                "}";

        this.mvc.perform(
                put("/adminusers/58").contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        contentJson = "{\n" +
                "    \"name\": \"李大力\",\n" +
                "    \"email\": \"kakaka@han-li.cn\",\n" +
                "    \"mobile\": \"13906008040\"\n" +
                "}";

        String responseString = this.mvc.perform(
                put("/adminusers/55").contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":733,\"errmsg\":\"电话已被注册\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试更新用户资料 (查无此用户)
     * @throws Exception Assert 或 HTTP 错误
     */
    @Test
    public void modifyNilUser() throws Exception {
        String contentJson = "{\n" +
                "    \"name\": \"李大力\",\n" +
                "    \"email\": \"ooad@han-li.cn\",\n" +
                "    \"mobile\": \"12345678\"\n" +
                "}";

        String responseString = this.mvc.perform(
                put("/adminusers/99").contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试更新用户资料 (用户已被删除)
     * @throws Exception Assert 或 HTTP 错误
     */
    @Test
    public void modifyDeletedUser() throws Exception {
        // 逻辑删除
        String responseString = this.mvc.perform(
                delete("/adminusers/58"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试硬更新
        String contentJson = "{\n" +
                "    \"name\": \"李大力\",\n" +
                "    \"email\": \"ooad@han-li.cn\",\n" +
                "    \"mobile\": \"12345678\"\n" +
                "}";

        responseString = this.mvc.perform(
                put("/adminusers/58").contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试 (逻辑) 删除用户
     * @throws Exception Assert 或 HTTP 错误
     */
    @Test
    public void deleteUser() throws Exception {

        String responseString = this.mvc.perform(
                delete("/adminusers/58"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试有关数据是否有真的删除
        UserPo updatedPo = userPoMapper.selectByPrimaryKey(58L);
        Assert.state(updatedPo.getState() == (byte) User.State.DELETE.getCode().intValue(), "这个用户并未被删除！");
    }

    /**
     * 测试删除用户 (查无此用户)
     * @throws Exception Assert 或 HTTP 错误
     */
    @Test
    public void deleteNilUser() throws Exception {

        String responseString = this.mvc.perform(
                delete("/adminusers/120"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试删除用户 (用户已被删除过)
     * @throws Exception Assert 或 HTTP 错误
     */
    @Test
    public void deleteDeletedUser() throws Exception {
        // 逻辑删除
        String responseString = this.mvc.perform(
                delete("/adminusers/55"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试还能否被删除
        responseString = this.mvc.perform(
                delete("/adminusers/55"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试封禁用户
     * @throws Exception Assert 或 HTTP 错误
     */
    @Test
    public void forbidUser() throws Exception {

        String responseString = this.mvc.perform(
                put("/adminusers/59/forbid"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试有关用户是否有真的被解封
        UserPo updatedPo = userPoMapper.selectByPrimaryKey(59L);
        Assert.state(updatedPo.getState() == (byte) User.State.FORBID.getCode().intValue(), "这个用户并未被封禁！");
    }

    /**
     * 测试封禁用户 (查无此用户)
     * @throws Exception Assert 或 HTTP 错误
     */
    @Test
    public void forbidNilUser() throws Exception {

        String responseString = this.mvc.perform(
                put("/adminusers/90/forbid"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试封禁用户 (查无此用户)
     * @throws Exception Assert 或 HTTP 错误
     */
    @Test
    public void forbidDeletedUser() throws Exception {
        // 逻辑删除
        String responseString = this.mvc.perform(
                delete("/adminusers/48"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试硬更改
        responseString = this.mvc.perform(
                put("/adminusers/48/forbid"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试解封用户
     * @throws Exception Assert 或 HTTP 错误
     */
    @Test
    public void releaseUser() throws Exception {

        String responseString = this.mvc.perform(
                put("/adminusers/59/release"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试有关用户是否有真的被解封
        UserPo updatedPo = userPoMapper.selectByPrimaryKey(59L);
        Assert.state(updatedPo.getState() == (byte) User.State.NORM.getCode().intValue(), "这个用户并未被解禁！");
    }

    /**
     * 测试解封用户 (查无此用户)
     * @throws Exception Assert 或 HTTP 错误
     */
    @Test
    public void releaseNilUser() throws Exception {

        String responseString = this.mvc.perform(
                put("/adminusers/321/release"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试解封用户 (已被删除的用户)
     * @throws Exception Assert 或 HTTP 错误
     */
    @Test
    public void releaseDeletedUser() throws Exception {
        // 逻辑删除
        String responseString = this.mvc.perform(
                delete("/adminusers/46"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试硬解封
        responseString = this.mvc.perform(
                put("/adminusers/46/release"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /* auth009 测试用例结束 */
}
