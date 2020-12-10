package cn.edu.xmu.order.controller;


import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.order.OrderServiceApplication;
import cn.edu.xmu.order.model.vo.OrderItemsCreateVo;
import cn.edu.xmu.order.model.vo.OrdersVo;
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

@SpringBootTest(classes = OrderServiceApplication.class)
public class OrderTest {
    private WebTestClient webTestClient;

    private MockMvc mvc;
    public OrderTest(){
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:8087")
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

    /**
     * @author Cai Xinlu
     * @date 2020-12-08 14:14
     */
    @Test
    public void getUserOrdersTest() throws Exception
    {
        String token=this.creatTestToken(1L,0L,100);

        byte[] responseString=webTestClient.get().uri("/orders?page=1&pageSize=1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":1,\"page\":1,\"list\":[{\"id\":1,\"customerId\":1,\"shopId\":1,\"orderSn\":\"2016102361242\",\"pid\":null,\"consignee\":\"刘勤\",\"regionId\":null,\"address\":null,\"mobile\":\"13959288888\",\"message\":null,\"orderType\":null,\"freightPrice\":null,\"couponId\":null,\"couponActivityId\":null,\"discountPrice\":null,\"originPrice\":null,\"presaleId\":null,\"grouponDiscount\":null,\"rebateNum\":null,\"confirmTime\":null,\"shipmentSn\":null,\"state\":6,\"substate\":null,\"beDeleted\":0,\"gmtCreated\":\"2020-12-06T22:44:31\",\"gmtModified\":\"2020-12-06T22:44:31\",\"orderItems\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);
    }

    @Test
    public String createOrdersTest() throws Exception
    {
//        String contentJson = "{\"orderItems\": [{ \"skuId\": 1,\"quantity\": 10,\"couponActId\": 110}]," +
//                "\"consignee\": \"string\",\"regionId\": 0, \"address\": \"string\",\"mobile\": \"string\"," +
//                "\"message\": \"string\",\"couponId\": 10,\"presaleId\": 0,\"grouponId\": 0}";
        String token = this.creatTestToken(1L,0L,100);
        OrdersVo ordersVo = new OrdersVo();
        List<OrderItemsCreateVo> orderItemsCreateVoList = new ArrayList<>();
        OrderItemsCreateVo orderItemsCreateVo = new OrderItemsCreateVo();
        orderItemsCreateVo.setCouponActivityId(0L);
        orderItemsCreateVo.setGoodsSkuId(1L);
        orderItemsCreateVo.setQuantity(5);
        orderItemsCreateVoList.add(orderItemsCreateVo);
        ordersVo.setOrderItems(orderItemsCreateVoList);
        ordersVo.setConsignee("caixin");
        ordersVo.setCouponId(10L);

        String requireJson = JacksonUtil.toJson(ordersVo);
        assert requireJson!=null;
        String response = this.mvc.perform(post("/orders")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8").content(requireJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andReturn().getResponse().getContentAsString();

        return  JacksonUtil.parseString(response, "data");
    }
}
