package cn.edu.xmu.order.controller;


import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.payment.PaymentServiceApplication;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = PaymentServiceApplication.class)
public class PaymentTest {

    private WebTestClient webTestClient;

    private MockMvc mvc;

    public PaymentTest(){
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:8088")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        //log.debug(token);
        return token;
    }

    @Test
    public void getRefundsByOrdersId() throws Exception
    {
        String token=this.creatTestToken(1L,0L,100);

        byte[] responseString=webTestClient.get().uri("/payment/orders/{id}/refunds",1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();
        String expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"id\":1,\"paymentId\":1,\"amount\":1,\"state\":null,\"gmtCreated\":\"2020-12-10T17:52:47\",\"gmtModified\":null,\"orderId\":1,\"aftersaleId\":1}}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);
    }
}
