package cn.edu.xmu.payment;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = PaymentServiceApplication.class)
public class PaymentTest2 {
    private WebTestClient webTestClient;

    public PaymentTest2(){
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:8088")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    /**
     *买家为售后单创建支付单
     * @author 洪晓杰
     */
    @Test
    public void createPaymentTestByAftersaleId() throws Exception{
        String token = this.creatTestToken(1L, 0L, 100);
        String paymentJson = "{\n" +
                "    \"price\": 1000,\n" +
                "    \"paymentPattern\": \"002\"\n" +
                "}";
        byte[] responseString = webTestClient.post().uri("/aftersales/{id}/payments",123)
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
