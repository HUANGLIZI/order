package cn.edu.xmu.freight.controller;

import cn.edu.xmu.freight.FreightServiceApplication;
import cn.edu.xmu.freight.model.vo.FreightModelChangeVo;
import cn.edu.xmu.freight.model.vo.PieceFreightModelChangeVo;
import cn.edu.xmu.freight.model.vo.WeightFreightModelChangeVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author Caixin
 * @date 2020-11-30 17:27
 */
@SpringBootTest(classes = FreightServiceApplication.class)   //标识本类是一个SpringBootTest
public class CaiXinluFreghtControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(CaiXinluFreghtControllerTest.class);

    //@Value("${public-test.managementgate}")
    private String managementGate="localhost:8080";

    //@Value("${public-test.mallgate}")
    private String mallGate="localhost:8086";

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    @BeforeEach
    public void setUp(){
        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    public String userLogin(String userName, String password) throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString)).getString("data");
    }

    private String adminLogin(String userName, String password) throws Exception{
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();

        byte[] ret = manageClient.post().uri("/privileges/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }

    @Test
    public void ChangeFreightModel1() throws Exception
    {
        String token = this.adminLogin("13088admin", "123456");
        FreightModelChangeVo freightModelChangeVo = new FreightModelChangeVo();
        freightModelChangeVo.setName("freightModeTest");
        freightModelChangeVo.setUnit(100);
        String freightJson = JacksonUtil.toJson(freightModelChangeVo);
        byte[] responseString =
                mallClient.put().uri("/freight/shops/{shopId}/freightmodels/{id}",1,1)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .returnResult()
                        .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    @Test
    public void ChangeFreightModel2() throws Exception
    {
        String token = this.adminLogin("9943200016", "123456");
        FreightModelChangeVo freightModelChangeVo = new FreightModelChangeVo();
        freightModelChangeVo.setName("freightModel");
        freightModelChangeVo.setUnit(100);
        String freightJson = JacksonUtil.toJson(freightModelChangeVo);
        byte[] responseString =
                mallClient.put().uri("/freight/shops/{shopId}/freightmodels/{id}",1,1)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .returnResult()
                        .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    @Test
    public void ChangeFreightModel3() throws Exception
    {
        String token = this.adminLogin("13088admin", "123456");
        FreightModelChangeVo freightModelChangeVo = new FreightModelChangeVo();
        freightModelChangeVo.setName("freightModel2");
        freightModelChangeVo.setUnit(100);
        String freightJson = JacksonUtil.toJson(freightModelChangeVo);
        byte[] responseString =
                mallClient.put().uri("/freight/shops/{shopId}/freightmodels/{id}",1,1)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .returnResult()
                        .getResponseBodyContent();

        String expectedResponse = "{\"errno\":802,\"errmsg\":\"运费模板名重复\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }


    /**
     * 运费模板中该地区已经定义  region已存在
     * @throws Exception
     */
    @Test
    public void ChangePieceFreightModel1() throws Exception
    {
        String token = this.adminLogin("13088admin", "123456");
        String freightJson = "{\n" +
                "    \"firstItems\": 60,\n" +
                "    \"firstItemsPrice\": 22,\n" +
                "    \"additionalItems\": 11,\n" +
                "    \"additionalItemsPrice\": 33,\n" +
                "    \"regionId\": 1\n" +
                "}";
        byte[] responseString =
                mallClient.put().uri("/freight/shops/{shopId}/pieceItems/{id}",1,1)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .returnResult()
                        .getResponseBodyContent();

        String expectedResponse = "{\"errno\":803,\"errmsg\":\"运费模板中该地区已经定义\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 路径上的shopId与Token中解析出来的不符
     * @throws Exception
     */
    @Test
    public void ChangePieceFreightModel2() throws Exception
    {
        String token = this.adminLogin("9943200016", "123456");
        String freightJson = "{\n" +
                "    \"firstItems\": 60,\n" +
                "    \"firstItemsPrice\": 22,\n" +
                "    \"additionalItems\": 11,\n" +
                "    \"additionalItemsPrice\": 33,\n" +
                "}";
        byte[] responseString =
                mallClient.put().uri("/freight/shops/{shopId}/pieceItems/{id}",1,1)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .returnResult()
                        .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 件数运费模板修改成功
     * @throws Exception
     */
    @Test
    public void ChangePieceFreightModel3() throws Exception
    {
        String token = this.adminLogin("13088admin", "123456");
        String freightJson = "{\n" +
                "    \"firstItems\": 60,\n" +
                "    \"firstItemsPrice\": 22,\n" +
                "    \"additionalItems\": 11,\n" +
                "    \"additionalItemsPrice\": 33,\n" +
                "}";
        byte[] responseString =
                mallClient.put().uri("/freight/shops/{shopId}/pieceItems/{id}",1,1)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .returnResult()
                        .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 运费模板中该地区已经定义  region已存在
     * @throws Exception
     */
    @Test
    public void ChangeWeightFreightModel1() throws Exception
    {
        String token = this.adminLogin("13088admin", "123456");
        String freightJson = "{\n" +
                "    \"firstWeightFreight\": 519,\n" +
                "    \"tenPrice\": 391,\n" +
                "    \"resionId\": 1\n" +
                "}";
        byte[] responseString =
                mallClient.put().uri("/freight/shops/{shopId}/weightItems/{id}",1,1)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .returnResult()
                        .getResponseBodyContent();

        String expectedResponse = "{\"errno\":803,\"errmsg\":\"运费模板中该地区已经定义\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 重量运费模板修改成功
     * @throws Exception
     */
    @Test
    public void ChangeWeightFreightModel2() throws Exception
    {
        String token = this.adminLogin("13088admin", "123456");
        String freightJson = "{\n" +
                "    \"firstWeightFreight\": 519,\n" +
                "    \"tenPrice\": 391,\n" +
                "}";
        byte[] responseString =
                mallClient.put().uri("/freight/shops/{shopId}/weightItems/{id}",1,1)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .returnResult()
                        .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 路径上的shopId与Token中解析出来的不符
     * @throws Exception
     */
    @Test
    public void ChangeWeightFreightModel3() throws Exception
    {
        String token = this.adminLogin("9943200016", "123456");
        String freightJson = "{\n" +
                "    \"firstWeightFreight\": 519,\n" +
                "    \"tenPrice\": 391,\n" +
                "}";
        byte[] responseString =
                mallClient.put().uri("/freight/shops/{shopId}/weightItems/{id}",1,1)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .returnResult()
                        .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
}
