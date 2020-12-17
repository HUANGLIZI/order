package cn.edu.xmu.payment;

import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

/**
 * @author Caixin
 * @date 2020-12-14 17:28
 */
@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
@Slf4j
public class CaiXinluTest {
    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

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

    private String login(String userName, String password) throws Exception{
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


    /**
     * 通过aftersaleId查找refund 成功
     */
    @Test
    public void getRefundTest1() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/aftersales/{id}/payments",1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .returnResult()
                        .getResponseBody();

        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":1,\"paymentId\":1,\"amount\":1,\"state\":null,\"gmtCreated\":\"2020-12-10T17:52:47\",\"gmtModified\":null,\"orderId\":1,\"aftersaleId\":1},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 通过aftersaleId查找refund  找不到路径上的aftersaleId
     */
    @Test
    public void getRefundTest2() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/aftersales/{id}/payments",666666)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBody();

        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":1,\"paymentId\":1,\"amount\":1,\"state\":null,\"gmtCreated\":\"2020-12-10T17:52:47\",\"gmtModified\":null,\"orderId\":1,\"aftersaleId\":1},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 通过aftersaleId查找refund  orderId不属于Token解析出来的userId
     */
    @Test
    public void getRefundTest3() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/payment/aftersales/{id}/payments",2)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .returnResult()
                        .getResponseBody();

        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 通过orderId查找refund  成功
     */
    @Test
    public void getRefundTest4() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/orders/{id}/refunds",1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBody();

        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":1,\"paymentId\":1,\"amount\":1,\"state\":null,\"gmtModified\":null,\"orderId\":1,\"aftersaleId\":1},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 通过orderId查找refund  找不到路径上的orderId
     */
    @Test
    public void getRefundTest5() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/orders/{id}/refunds",666666)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBody();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 通过orderId查找refund  orderId不属于Token解析出来的userId
     */
    @Test
    public void getRefundTest6() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/orders/{id}/refunds",2)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .returnResult()
                        .getResponseBody();

        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 通过aftersaleId和shopId查找refund  通过aftersaleId找shopId 返回的shopId与路径上的shopId不符
     */
    @Test
    public void getRefundTest7() throws Exception{
        String token = this.login("9943200016", "123456");
        byte[] responseString =
                mallClient.get().uri("/shops/{shopId}/aftersales/{id}/refunds",666666,1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .returnResult()
                        .getResponseBody();

        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 通过aftersaleId和shopId查找refund  成功
     */
    @Test
    public void getRefundTest8() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/shops/{shopId}/aftersales/{id}/refunds",1,1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .returnResult()
                        .getResponseBody();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 通过aftersaleId和shopId查找refund  找不到aftersaleId
     */
    @Test
    public void getRefundTest9() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/shops/{shopId}/aftersales/{id}/refunds",1,666666)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody()
                        .returnResult()
                        .getResponseBody();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 通过orderId和shopId查找refund  通过orderId找shopId 返回的shopId与路径上的shopId不符
     */
    @Test
    public void getRefundTest10() throws Exception{
        String token = this.login("9943200016", "123456");
        byte[] responseString =
                mallClient.get().uri("/shops/{shopId}/orders/{id}/refunds",666666,1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .returnResult()
                        .getResponseBody();

        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 通过orderId和shopId查找refund  成功
     */
    @Test
    public void getRefundTest11() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/shops/{shopId}/orders/{id}/refunds",1,1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .returnResult()
                        .getResponseBody();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 通过orderId和shopId查找refund  找不到orderId
     */
    @Test
    public void getRefundTest12() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString =
                mallClient.get().uri("/shops/{shopId}/orders/{id}/refunds",1,666666)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody()
                        .returnResult()
                        .getResponseBody();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }
}
