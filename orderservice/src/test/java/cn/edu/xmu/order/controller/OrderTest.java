package cn.edu.xmu.order.controller;


import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.order.OrderServiceApplication;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = OrderServiceApplication.class)
public class OrderTest {
    private WebTestClient webTestClient;

    public OrderTest(){
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
    public void shopUpdateOrderTest() throws Exception{
        String token=this.creatTestToken(1L,0L,100);
        String json="{ \"message\": \"test\"}";
        byte[] responseString=webTestClient.put().uri("/shops/{shopId}/orders/{id}",0,1)
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();

        String expectedResponse="{ \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);


    }
    /**
     * @author zxj
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    public void shopDeliverOrderTest() throws Exception{
        String token=this.creatTestToken(1L,0L,100);

        byte[] responseString=webTestClient.put().uri("/shops/{shopId}/orders/{id}/deliver",0,1)
                .header("authorization", token)
                .bodyValue("{\"freightSn\": \"123456\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();

        String expectedResponse="{ \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);


    }


    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        //log.debug(token);
        return token;
    }

}
