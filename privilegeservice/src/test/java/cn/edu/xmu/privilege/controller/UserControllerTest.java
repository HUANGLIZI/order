package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author XQChen
 * @date Created in 2020/11/4 0:33
 **/
@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    private String content;

    private final JwtHelper jwtHelper = new JwtHelper();

    @Test
    public void findAllUsers() throws Exception {
        //参数正确的请求
        try
        {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/findAllUsersSuccess.json")));
        } catch (Exception e) {e.printStackTrace();}

        System.out.println("content: " + content);

        String responseSuccessString = this.mvc.perform(get("/adminusers/all?userName=&mobile=&page=1&pagesize=3").header("token", ""))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(responseSuccessString, content, true);

        //参数错误的请求
        try
        {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/findAllUsersFail.json")));
        } catch (Exception e) {e.printStackTrace();}

        System.out.println("content: " + content);

        String responseFailString = this.mvc.perform(get("/adminusers/all?userName=&mobile=&page=1&pagesize=-2").header("token", ""))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(responseFailString, content, true);
    }

    @Test
    public void findUserSelf() throws Exception {
        try
        {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/findUserSelf.json")));
        } catch (Exception e) {e.printStackTrace();}

        String responseString = this.mvc.perform(get("/adminusers").header("token", ""))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(responseString, content, true);
    }

    @Test
    public void findUserById() throws Exception {
        //ID存在的请求
        try
        {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/findUserByIdSuccess.json")));
        } catch (Exception e) {e.printStackTrace();}

        String responseSuccessString = this.mvc.perform(get("/adminusers/46").header("token", ""))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(responseSuccessString, content, true);

        //ID不存在的请求
        try
        {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/findUserByIdFail.json")));
        } catch (Exception e) {e.printStackTrace();}

        String responseFailString = this.mvc.perform(get("/adminusers/46").header("token", ""))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(responseFailString, content, true);
    }
}