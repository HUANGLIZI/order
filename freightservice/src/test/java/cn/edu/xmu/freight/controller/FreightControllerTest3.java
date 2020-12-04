package cn.edu.xmu.freight.controller;

import cn.edu.xmu.freight.FreightServiceApplication;
import cn.edu.xmu.freight.dao.FreightDao;
import cn.edu.xmu.freight.model.vo.PieceFreightModelVo;
import cn.edu.xmu.freight.model.vo.WeightFreightModelVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 运费模板控制器测试类
 *
 * @author 24320182203196 洪晓杰
 **/
@SpringBootTest(classes = FreightServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc    //配置模拟的MVC，这样可以不启动服务器测试
@Transactional
public class FreightControllerTest3 {


    @Autowired
    private FreightDao freightDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

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

    /**
     * 管理员设置默认运费模板 成功
     * @author 24320182203196 洪晓杰
     */
    @Test
    public void setupDefaultModelTest1() {

        String token = creatTestToken(1L, 0L, 100);
        //String roleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;
        try {

            responseString = this.mvc.perform(post("/shops/12/freight_models/8/default").header("authorization", token).contentType("application/json;charset=utf-8"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            System.out.println(responseString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    /**
     * 管理员设置默认运费模板 因为目标默认模板已存在，所以插入失败
     * @author 24320182203196 洪晓杰
     */
    @Test
    public void setupDefaultModelTest2() {
        String token = creatTestToken(1L, 0L, 100);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/shops/12/freight_models/11/default").header("authorization", token).contentType("application/json;charset=utf-8"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            System.out.println(responseString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":826,\"errmsg\":\"已经存在对应的默认模板，新增失败\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 管理员设置默认运费模板 不存在对应的shopid，所以插入失败
     * @author 24320182203196 洪晓杰
     */
    @Test
    public void setupDefaultModelTest3() {
        String token = creatTestToken(1L, 0L, 100);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/shops/5/freight_models/11/default").header("authorization", token).contentType("application/json;charset=utf-8"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            System.out.println(responseString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":825,\"errmsg\":\"不存在对应的shopid\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 管理员设置默认运费模板 shopid不存在对应的模板id，所以插入失败
     * @author 24320182203196 洪晓杰
     */
    @Test
    public void setupDefaultModelTest4() {
        String token = creatTestToken(1L, 0L, 100);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/shops/12/freight_models/13/default").header("authorization", token).contentType("application/json;charset=utf-8"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            System.out.println(responseString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":827,\"errmsg\":\"shopid不存在对应的模板id，新增失败\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    /**
     * 管理员定义重量模板明细
     * shopid不存在对应的模板id，所以插入失败
     * @author 24320182203196 洪晓杰
     */
    @Test
    public void insertWeightFreightModelTest5() {
        WeightFreightModelVo vo = new WeightFreightModelVo();
        vo.setFirstWeight((long)0);
        vo.setFirstWeightFreight((long)0);
        vo.setTenPrice((long)0);
        vo.setHundredPrice((long)0);
        vo.setTrihunPrice((long)0);
        vo.setFiftyPrice((long)0);
        vo.setAbovePrice((long)0);
        vo.setRegionId((long)0);

        String token = creatTestToken(1L, 0L, 100);
        String weightFreightModelJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/shops/234/freightmodels/2314/weightItems").header("authorization", token).contentType("application/json;charset=UTF-8").content(weightFreightModelJson))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            System.out.println(responseString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"data\":{\"freightModelId\":2314,\"firstWeight\":0,\"firstWeightFreight\":0,\"tenPrice\":0,\"fiftyPrice\":0,\"hundredPrice\":0,\"trihunPrice\":0,\"abovePrice\":0,\"regionId\":0},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 管理员定义件数模板明细
     * 插入成功
     * @author 24320182203196 洪晓杰
     */
    @Test
    public void insertPieceFreightModelTest6() {
        PieceFreightModelVo vo=new PieceFreightModelVo();
        vo.setAdditionalItems(0);
        vo.setAdditionalItemsPrice((long)0);
        vo.setFirstItems(0);
        vo.setRegionId((long)0);
        vo.setFirstItemsPrice((long)0);

        String token = creatTestToken(1L, 0L, 100);
        String pieceFreightModelJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/shops/123/freightmodels/123/pieceItems").header("authorization", token).contentType("application/json;charset=utf-8").content(pieceFreightModelJson))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            System.out.println(responseString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
