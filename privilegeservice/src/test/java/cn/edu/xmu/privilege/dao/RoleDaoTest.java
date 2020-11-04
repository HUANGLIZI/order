package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import cn.edu.xmu.privilege.model.vo.RoleVo;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * @author Ming Qiu
 * @date Created in 2020/11/3 15:23
 **/
@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc    //配置模拟的MVC，这样可以不启动服务器测试
@Transactional
public class RoleDaoTest {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void loadRolePriv1(){

        roleDao.loadRolePriv(Long.valueOf(23));

        String key = "r_23";
        assertTrue(redisTemplate.hasKey(key));
        assertTrue(redisTemplate.opsForSet().isMember(key,"2"));
        assertTrue(redisTemplate.opsForSet().isMember(key,"3"));
        assertTrue(redisTemplate.opsForSet().isMember(key,"4"));
        assertTrue(redisTemplate.opsForSet().isMember(key,"5"));
        assertFalse(redisTemplate.opsForSet().isMember(key,"1"));
        assertEquals(16, redisTemplate.opsForSet().size(key));
    }

    @Test
    public void loadRolePriv2(){
        roleDao.loadRolePriv(Long.valueOf(80));
        assertTrue(redisTemplate.hasKey("r_80"));
        assertTrue(redisTemplate.opsForSet().isMember("r_80","0"));
        assertEquals(1, redisTemplate.opsForSet().size("r_80"));
    }

    private static final Logger logger = LoggerFactory.getLogger(RoleDaoTest.class);
    @Autowired
    private MockMvc mvc;

    @Test
    public void loadRolePriv3(){

        roleDao.loadRolePriv(Long.valueOf(84));

        String key = "r_84";
        assertTrue(redisTemplate.hasKey(key));
        assertTrue(redisTemplate.opsForSet().isMember(key, "14"));
        assertFalse(redisTemplate.opsForSet().isMember(key, "2"));
        assertEquals(1, redisTemplate.opsForSet().size(key));

    }

    /**
     *新建token
     */
    @Test
    public void createToken() {
        JwtHelper jwtHelper = new JwtHelper();
        String token = jwtHelper.createToken(1L, 0L);
        logger.info(token);
    }

    /**
     *查询角色 成功
     */
    @Test
    public void selectRoleTest() {
        String responseString = null;
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDQ0ODIwMzAsInVzZXJJZCI6MSwiaWF0IjoxNjA0NDc0ODMwfQ.f1g65bUbkK8FlyW9ac5WhM9FBT6ILOmGROjuMVJntyE";
        try {
            responseString = this.mvc.perform(get("/roles?page=1&pageSize=2").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"data\":[{\"id\":23,\"name\":\"管理员\",\"desc\":\"超级管理员，所有权限都有\",\"createdBy\":1,\"gmtCreate\":\"2020-11-01T09:48:24\",\"gmtModified\":\"2020-11-01T09:48:24\"},{\"id\":80,\"name\":\"财务\",\"desc\":null,\"createdBy\":1}],\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *插入角色 成功
     */
    @Test
    public void insertRoleTest1() {
        RoleVo vo = new RoleVo();
        vo.setName("test");
        vo.setDescr("test");
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDQ0ODIwMzAsInVzZXJJZCI6MSwiaWF0IjoxNjA0NDc0ODMwfQ.f1g65bUbkK8FlyW9ac5WhM9FBT6ILOmGROjuMVJntyE";
        String roleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(post("/roles").header("authorization", token).contentType("application/json;charset=UTF-8").content(roleJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"data\":{\"id\":88,\"name\":\"test\",\"desc\":\"test\",\"createdBy\":1},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *插入角色 角色名已存在
     */
    @Test
    public void insertRoleTest2() {
        RoleVo vo = new RoleVo();
        vo.setName("管理员");
        vo.setDescr("管理员test");
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDQ0ODIwMzAsInVzZXJJZCI6MSwiaWF0IjoxNjA0NDc0ODMwfQ.f1g65bUbkK8FlyW9ac5WhM9FBT6ILOmGROjuMVJntyE";
        String roleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(post("/roles").header("authorization", token).contentType("application/json;charset=UTF-8").content(roleJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":736,\"errmsg\":\"角色名已存在\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *插入角色 角色名为空
     */
    @Test
    public void insertRoleTest3() {
        RoleVo vo = new RoleVo();
        vo.setName("");
        vo.setDescr("test");
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDQ0ODIwMzAsInVzZXJJZCI6MSwiaWF0IjoxNjA0NDc0ODMwfQ.f1g65bUbkK8FlyW9ac5WhM9FBT6ILOmGROjuMVJntyE";
        String roleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(post("/roles").header("authorization", token).contentType("application/json;charset=UTF-8").content(roleJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":503,\"errmsg\":\"角色名不能为空;\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *修改角色 成功
     */
    @Test
    public void updateRoleTest1() {
        RoleVo vo = new RoleVo();
        vo.setName("test");
        vo.setDescr("test");
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDQ0ODIwMzAsInVzZXJJZCI6MSwiaWF0IjoxNjA0NDc0ODMwfQ.f1g65bUbkK8FlyW9ac5WhM9FBT6ILOmGROjuMVJntyE";
        String roleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(put("/roles/87").header("authorization", token).contentType("application/json;charset=UTF-8").content(roleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *修改角色 角色名为空
     */
    @Test
    public void updateRoleTest2() {
        RoleVo vo = new RoleVo();
        vo.setName("");
        vo.setDescr("test");
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDQ0ODIwMzAsInVzZXJJZCI6MSwiaWF0IjoxNjA0NDc0ODMwfQ.f1g65bUbkK8FlyW9ac5WhM9FBT6ILOmGROjuMVJntyE";
        String roleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(put("/roles/87").header("authorization", token).contentType("application/json;charset=UTF-8").content(roleJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":503,\"errmsg\":\"角色名不能为空;\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *修改角色 id不存在
     */
    @Test
    public void updateRoleTest3() {
        RoleVo vo = new RoleVo();
        vo.setName("test");
        vo.setDescr("test");
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDQ0ODIwMzAsInVzZXJJZCI6MSwiaWF0IjoxNjA0NDc0ODMwfQ.f1g65bUbkK8FlyW9ac5WhM9FBT6ILOmGROjuMVJntyE";
        String roleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(put("/roles/0").header("authorization", token).contentType("application/json;charset=UTF-8").content(roleJson))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *修改角色 角色名重复
     */
    @Test
    public void updateRoleTest4() {
        RoleVo vo = new RoleVo();
        vo.setName("管理员");
        vo.setDescr("管理员test");
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDQ0ODIwMzAsInVzZXJJZCI6MSwiaWF0IjoxNjA0NDc0ODMwfQ.f1g65bUbkK8FlyW9ac5WhM9FBT6ILOmGROjuMVJntyE";
        String roleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(put("/roles/87").header("authorization", token).contentType("application/json;charset=UTF-8").content(roleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":736,\"errmsg\":\"角色名已存在\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *删除角色 成功
     */
    @Test
    public void deleteRoleTest1() {
        // chushihua
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDQ0ODIwMzAsInVzZXJJZCI6MSwiaWF0IjoxNjA0NDc0ODMwfQ.f1g65bUbkK8FlyW9ac5WhM9FBT6ILOmGROjuMVJntyE";
        String expectedResponse = "";
        String responseString = null;
        //ceshichenggongshanchu
        try {
            responseString = this.mvc.perform(delete("/roles/87").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *删除角色 id不存在
     */
    @Test
    public void deleteRoleTest2() {
        // chushihua
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDQ0ODIwMzAsInVzZXJJZCI6MSwiaWF0IjoxNjA0NDc0ODMwfQ.f1g65bUbkK8FlyW9ac5WhM9FBT6ILOmGROjuMVJntyE";
        String expectedResponse = "";
        String responseString = null;
        //ceshiwuid
        try {
            responseString = this.mvc.perform(delete("/roles/0").header("authorization", token))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
