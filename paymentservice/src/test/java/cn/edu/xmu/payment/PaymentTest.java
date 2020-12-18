package cn.edu.xmu.payment;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JsonContentAssert;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = PaymentServiceApplication.class)
public class PaymentTest {

    private WebTestClient webTestClient;

    public PaymentTest(){
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:8088")
//                .baseUrl("http://172.20.10.5:8088")
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
        byte[] responseString=webTestClient.get().uri("/orders/{id}/payments",1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();

        System.out.println(new String(responseString,"UTF-8"));
        String expectedResponse="{\"errno\":0,\"data\":[{\"id\":1,\"amout\":0,\"actualAmount\":0,\"paymentPattern\":\"0\",\"payTime\":\"2020-12-10T19:29:50\",\"beginTime\":\"2020-12-10T19:29:50\",\"endTime\":\"2020-12-10T19:29:50\",\"orderId\":1,\"aftersaleId\":null,\"state\":0,\"gmtCreated\":\"2020-12-10T19:29:50\",\"gmtModified\":\"2020-12-10T19:29:50\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);
    }
    /**
     * @author zxj
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    public void userQueryPaymentTest2() throws Exception{
        String token=this.creatTestToken(1L,0L,100);
        byte[] responseString=webTestClient.get().uri("/orders/{id}/payments",21000)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBody();

        System.out.println(new String(responseString,"UTF-8"));
        String expectedResponse="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);
    }
    /**
     * @author zxj
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    public void queryPaymentTest() throws Exception{
        String token=this.creatTestToken(1L,0L,100);
        byte[] responseString=webTestClient.get().uri("/shops/{shopId}/orders/{id}/payments",1,1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();

        System.out.println(new String(responseString,"UTF-8"));
        String expectedResponse="{\"errno\":0,\"data\":[{\"id\":1,\"amout\":0,\"actualAmount\":0,\"paymentPattern\":\"0\",\"payTime\":\"2020-12-10T19:29:50\",\"beginTime\":\"2020-12-10T19:29:50\",\"endTime\":\"2020-12-10T19:29:50\",\"orderId\":1,\"aftersaleId\":null,\"state\":0,\"gmtCreated\":\"2020-12-10T19:29:50\",\"gmtModified\":\"2020-12-10T19:29:50\"}],\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);


    }

        /**
     * @author zxj
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    public void queryPaymentTest1() throws Exception{
        String token=this.creatTestToken(1L,0L,100);
        byte[] responseString=webTestClient.get().uri("/shops/{shopId}/orders/{id}/payments",2,1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage())
                .returnResult()
                .getResponseBody();

        System.out.println(new String(responseString,"UTF-8"));
    }

    @Test
    public void queryPaymentTest2() throws Exception{
        String token=this.creatTestToken(1L,0L,100);
        byte[] responseString=webTestClient.get().uri("/shops/{shopId}/orders/{id}/payments",0,39000)
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

    /**
     * 根据aftersaleId查询refund 正确
     * @author Cai Xinlu
     * @date 2020-12-13 21:30
     */
    @Test
    public void getRefundTest1() throws Exception{
        String token=this.creatTestToken(1L,0L,100);
        byte[] responseString=webTestClient.get().uri("/payment/aftersales/{id}/refunds",1)
                .header("authorization", token)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();

        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":1,\"paymentId\":1,\"amount\":1,\"state\":null,\"gmtCreated\":\"2020-12-10T17:52:47\",\"gmtModified\":null,\"orderId\":1,\"aftersaleId\":1},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-13 21:30
     */
    @Test
    public void getRefundTest2() throws Exception{
        String token=this.creatTestToken(1L,0L,100);
        byte[] responseString=webTestClient.get().uri("/payment/aftersales/{id}/refunds",2)
                .header("authorization", token)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();

        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":1,\"paymentId\":1,\"amount\":1,\"state\":null,\"gmtCreated\":\"2020-12-10T17:52:47\",\"gmtModified\":null,\"orderId\":1,\"aftersaleId\":1},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * @author zxj
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    public void getAllPaymentsStatesTest() throws Exception{
        //String token=this.creatTestToken(1L,0L,100);
        byte[] responseString=webTestClient.get().uri("/payments/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();

        String expectedResponse="{\"errno\":0,\"data\":[{\"name\":\"支付单未支付\",\"code\":0},{\"name\":\"支付单已支付\",\"code\":1},{\"name\":\"支付失败\",\"code\":2}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);
    }

    /**
     * @author zxj
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    public void getPaymentPatternsTest() throws Exception{
        String token=this.creatTestToken(1L,0L,100);
        byte[] responseString=webTestClient.get().uri("/payments/patterns")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();

        String expectedResponse="{\"errno\":0,\"data\":[{\"name\":\"返点支付\",\"payPattern\":\"001\"},{\"name\":\"模拟支付渠道\",\"payPattern\":\"002\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);
    }
}


