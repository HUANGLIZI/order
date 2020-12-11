package cn.edu.xmu.payment;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JsonContentAssert;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = PaymentServiceApplication.class)
public class PaymentTest {
    private WebTestClient webTestClient;

    public PaymentTest(){
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:8080")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    /**
     * @author zxj
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    public void userQueryPaymentTest() throws Exception{
        String token=this.creatTestToken(1L,0L,100);
        byte[] responseString=webTestClient.get().uri("/payment/orders/{id}/payments",123)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();

        String expectedResponse="{\"errno\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"id\": 108,\n" +
                "            \"amout\": null,\n" +
                "            \"actualAmount\": 0,\n" +
                "            \"paymentPattern\": 0,\n" +
                "            \"payTime\": \"2020-11-24T18:40:25\",\n" +
                "            \"paySn\": null,\n" +
                "            \"beginTime\": \"2020-11-24T18:40:25\",\n" +
                "            \"endTime\": \"2020-11-24T18:40:25\",\n" +
                "            \"orderId\": 123,\n" +
                "            \"aftersaleId\": 123456789,\n" +
                "            \"state\": 0,\n" +
                "            \"gmtCreated\": \"2020-11-24T18:40:25\",\n" +
                "            \"gmtModified\": \"2020-11-24T18:40:25\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 109,\n" +
                "            \"amout\": null,\n" +
                "            \"actualAmount\": 2399,\n" +
                "            \"paymentPattern\": 0,\n" +
                "            \"payTime\": \"2020-11-24T18:40:25\",\n" +
                "            \"paySn\": null,\n" +
                "            \"beginTime\": \"2020-11-24T18:40:25\",\n" +
                "            \"endTime\": \"2020-11-24T18:40:25\",\n" +
                "            \"orderId\": 123,\n" +
                "            \"aftersaleId\": 123456789,\n" +
                "            \"state\": 0,\n" +
                "            \"gmtCreated\": \"2020-11-24T18:40:25\",\n" +
                "            \"gmtModified\": \"2020-11-24T18:40:25\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);


    }

    /**
     * @author zxj
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    public void queryPaymentTest() throws Exception{
        String token=this.creatTestToken(1L,0L,100);
        byte[] responseString=webTestClient.get().uri("/payment/shops/{shopId}/orders/{id}/payments",1010,123)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();

        String expectedResponse="{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"id\": 108,\n" +
                "            \"amout\": null,\n" +
                "            \"actualAmount\": 0,\n" +
                "            \"paymentPattern\": 0,\n" +
                "            \"payTime\": \"2020-11-24T18:40:25\",\n" +
                "            \"paySn\": null,\n" +
                "            \"beginTime\": \"2020-11-24T18:40:25\",\n" +
                "            \"endTime\": \"2020-11-24T18:40:25\",\n" +
                "            \"orderId\": 123,\n" +
                "            \"aftersaleId\": 123456789,\n" +
                "            \"state\": 0,\n" +
                "            \"gmtCreated\": \"2020-11-24T18:40:25\",\n" +
                "            \"gmtModified\": \"2020-11-24T18:40:25\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 109,\n" +
                "            \"amout\": null,\n" +
                "            \"actualAmount\": 2399,\n" +
                "            \"paymentPattern\": 0,\n" +
                "            \"payTime\": \"2020-11-24T18:40:25\",\n" +
                "            \"paySn\": null,\n" +
                "            \"beginTime\": \"2020-11-24T18:40:25\",\n" +
                "            \"endTime\": \"2020-11-24T18:40:25\",\n" +
                "            \"orderId\": 123,\n" +
                "            \"aftersaleId\": 123456789,\n" +
                "            \"state\": 0,\n" +
                "            \"gmtCreated\": \"2020-11-24T18:40:25\",\n" +
                "            \"gmtModified\": \"2020-11-24T18:40:25\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);


    }

        /**
     * @author zxj
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    public void queryPaymentTest0() throws Exception{
        String token=this.creatTestToken(1L,0L,100);
        byte[] responseString=webTestClient.get().uri("/payment/shops/{shopId}/orders/{id}/payments",1011,123)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBody();

    }


    /**
     * @author zxj
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    public void createPaymentTest() throws Exception{
        String token = this.creatTestToken(1L, 0L, 100);
        String paymentJson = "{\n" +
                "    \"price\": 1000,\n" +
                "    \"paymentPattern\": \"002\"\n" +
                "}";
        byte[] responseString = webTestClient.post().uri("/payment/orders/{id}/payments",123)
                .header("authorization", token)
                .bodyValue(paymentJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"id\": 20835,\n" +
                "        \"amout\": 1000,\n" +
                "        \"actualAmount\": 1000,\n" +
                "        \"paymentPattern\": 2,\n" +
                "        \"paySn\": null,\n" +
                "        \"orderId\": 123,\n" +
                "        \"aftersaleId\": null,\n" +
                "        \"state\": 0\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        //log.debug(token);
        return token;
    }
}


