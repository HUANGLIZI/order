package cn.edu.xmu.order.controller;


import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.order.OrderServiceApplication;
import cn.edu.xmu.order.model.vo.OrderItemsCreateVo;
import cn.edu.xmu.order.model.vo.OrdersVo;
import cn.edu.xmu.privilege.model.vo.LoginVo;
import org.junit.jupiter.api.Order;
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

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    private String managementGate="http://localhost:8080";

    //@Value("${public-test.mallgate}")
    private String mallGate="localhost:8087";

    private MockMvc mvc;
    public OrderTest(){
        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:8087")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();


        this.manageClient = WebTestClient.bindToServer()
                .baseUrl(managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

//    /**
//     * 1
//     * @author zxj
//     * @date Created in 2020年12月3日20:32:07
//     */
//    @Test
//    public void shopUpdateOrderTest1() throws Exception{
//        String token=this.login("537300010","123456");
//        String json="{ \"message\": \"test\"}";
//        byte[] responseString=mallClient.put().uri("/shops/{shopId}/orders/{id}",1,48050)
//                .header("authorization", token)
//                .bodyValue(json)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
//                .returnResult()
//                .getResponseBody();
//
//        //查询
//        byte[] responseString1=mallClient.get().uri("/shops/{shopId}/orders/{id}",1,48050)
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
//                .returnResult()
//                .getResponseBody();
//        String expectedResponse="{\"errno\":0,\"data\":{\"id\":48050,\"message\":\"test\"},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse,new String(responseString1, StandardCharsets.UTF_8),false);
//    }
//
//
//    /**
//     * 2
//     * 不是自己店铺的资源
//     * @author zxj
//     * @date Created in 2020年12月3日20:32:07
//     */
//    @Test
//    public void shopUpdateOrderTest2() throws Exception{
//        String token=this.login("537300010","123456");
//        String json="{ \"message\": \"test\"}";
//        byte[] responseString=webTestClient.put().uri("/shops/{shopId}/orders/{id}",1,40000)
//                .header("authorization", token)
//                .bodyValue(json)
//                .exchange()
//                .expectStatus().isForbidden()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
//                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage())
//                .returnResult()
//                .getResponseBody();
//    }
//
//    /**
//     * 3
//     * 不存在这个资源
//     * @author zxj
//     * @date Created in 2020年12月3日20:32:07
//     */
//    @Test
//    public void shopUpdateOrderTest3() throws Exception{
//        String token=this.login("13088admin","123456");
//        String json="{ \"message\": \"test\"}";
//        byte[] responseString=webTestClient.put().uri("/shops/{shopId}/orders/{id}",123,100000)
//                .header("authorization", token)
//                .bodyValue(json)
//                .exchange()
//                .expectStatus().isNotFound()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
//                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
//                .returnResult()
//                .getResponseBody();
//    }
//
//    /**
//     * 4
//     * 商铺将支付完成的订单改为发货状态(状态码为10、11、12的订单才能修改为发货状态)
//     * @author zxj
//     * @date Created in 2020年12月3日20:32:07
//     */
//    @Test
//    public void shopDeliverOrderTest1() throws Exception{
//        String token=this.login("13088admin","123456");
//
//        byte[] responseString=webTestClient.put().uri("/shops/{shopId}/orders/{id}/deliver",123,40000)
//                .header("authorization", token)
//                .bodyValue("{\"freightSn\": \"123456\"}")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
//                .returnResult()
//                .getResponseBody();
//
//        //查询
//        byte[] responseString1=webTestClient.get().uri("/shops/{shopId}/orders/{id}",123,40000)
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
//                .returnResult()
//                .getResponseBody();
//        System.out.println(new String(responseString1, StandardCharsets.UTF_8));
//        String expectedResponse="{\"errno\":0,\"data\":{\"id\":40000,\"shipmentSn\":\"123456\",\"state\":16},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse,new String(responseString1, StandardCharsets.UTF_8),false);
//    }
//
//    /**
//     * 5
//     * 商铺将支付完成的订单改为发货状态(状态码为10、11、12的订单才能修改为发货状态),但订单状态不为10、11、13无法完成转化
//     * @author zxj
//     * @date Created in 2020年12月3日20:32:07
//     */
//    @Test
//    public void shopDeliverOrderTest2() throws Exception {
//        String token = this.login("13088admin", "123456");
//
//        byte[] responseString = webTestClient.put().uri("/shops/{shopId}/orders/{id}/deliver", 123, 40001)
//                .header("authorization", token)
//                .bodyValue("{\"freightSn\": \"123456\"}")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
//                .returnResult()
//                .getResponseBody();
//
//    }
//
//
//    private final String creatTestToken(Long userId, Long departId, int expireTime) {
//        String token = new JwtHelper().createToken(userId, departId, expireTime);
//        //log.debug(token);
//        return token;
//    }
//
//    /**
//     * @author Cai Xinlu
//     * @date 2020-12-08 14:14
//     */
//    @Test
//    public void getUserOrdersTest() throws Exception
//    {
//        String token=this.creatTestToken(1L,0L,100);
//
//        byte[] responseString=webTestClient.get().uri("/orders?page=1&pageSize=1")
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
//                .returnResult()
//                .getResponseBody();
////        String expectedResponse="{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":1,\"page\":1,\"list\":[{\"id\":1,\"customerId\":1,\"shopId\":1,\"orderSn\":\"2016102361242\",\"pid\":null,\"consignee\":\"刘勤\",\"regionId\":null,\"address\":null,\"mobile\":\"13959288888\",\"message\":null,\"orderType\":null,\"freightPrice\":null,\"couponId\":null,\"couponActivityId\":null,\"discountPrice\":null,\"originPrice\":null,\"presaleId\":null,\"grouponDiscount\":null,\"rebateNum\":null,\"confirmTime\":null,\"shipmentSn\":null,\"state\":6,\"substate\":null,\"beDeleted\":0,\"gmtCreated\":\"2020-12-06T22:44:31\",\"gmtModified\":\"2020-12-06T22:44:31\",\"orderItems\":null}]},\"errmsg\":\"成功\"}";
//        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);
//    }
//
//
//    /**
//     * @param
//     * @return
//     * @author Cai Xinlu
//     * @date 2020-12-10 17:39
//     */
//    @Test
//    public String createOrdersTest() throws Exception
//    {
////        String contentJson = "{\"orderItems\": [{ \"skuId\": 1,\"quantity\": 10,\"couponActId\": 110}]," +
////                "\"consignee\": \"string\",\"regionId\": 0, \"address\": \"string\",\"mobile\": \"string\"," +
////                "\"message\": \"string\",\"couponId\": 10,\"presaleId\": 0,\"grouponId\": 0}";
//        String token = this.creatTestToken(1L,0L,100);
//        OrdersVo ordersVo = new OrdersVo();
//        List<OrderItemsCreateVo> orderItemsCreateVoList = new ArrayList<>();
//        OrderItemsCreateVo orderItemsCreateVo = new OrderItemsCreateVo();
//        orderItemsCreateVo.setCouponActivityId(0L);
//        orderItemsCreateVo.setGoodsSkuId(1L);
//        orderItemsCreateVo.setQuantity(5);
//        orderItemsCreateVoList.add(orderItemsCreateVo);
//        ordersVo.setOrderItems(orderItemsCreateVoList);
//        ordersVo.setConsignee("caixin");
//        ordersVo.setCouponId(10L);
//
//        String requireJson = JacksonUtil.toJson(ordersVo);
//        assert requireJson!=null;
//        String response = this.mvc.perform(post("/orders")
//                .header("authorization", token)
//                .contentType("application/json;charset=UTF-8").content(requireJson))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
//                .andExpect(jsonPath("$.errmsg").value("成功"))
//                .andReturn().getResponse().getContentAsString();
//
//        return  JacksonUtil.parseString(response, "data");
//
//    }

    @Test
    public void shopsShopIdOrdersIdPut0() throws Exception {
        String orderJson="{\n" +
                "  \"message\": \"test\"\n" +
                "}";
        String token=this.login("537300010","123456");

        byte[] responseString = mallClient.put().uri("/shops/1/orders/48050")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

        byte[] confirmString = mallClient.get().uri("/shops/1/orders/48050")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedConResponse = "{data:{id:48050,message:test}}";

        JSONAssert.assertEquals(expectedConResponse, new String(confirmString, StandardCharsets.UTF_8), false);

    }

    @Test
    @Order(8)
    public void shopsShopIdOrdersIdPut1() throws Exception {
        String orderJson="{\n" +
                "  \"message\": \"test\"\n" +
                "}";
        String token=this.login("537300010","123456");

        byte[] responseString = mallClient.put().uri("/shops/1/orders/4000000")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家修改订单留言 订单非本店铺订单
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(9)
    public void shopsShopIdOrdersIdPut2() throws Exception {
        String orderJson="{\n" +
                "  \"message\": \"test\"\n" +
                "}";
        String token=this.login("537300010","123456");

        byte[] responseString = mallClient.put().uri("/shops/1/orders/1")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    @Test
    @Order(10)
    public void postFreights0() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token=this.login("537300010","123456");

        byte[] responseString = mallClient.put().uri("/shops/1/orders/48052/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

        byte[] confirmString = mallClient.get().uri("/shops/1/orders/48052")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedConResponse = "{data:{id:48052,shipmentSn:test}}";

        JSONAssert.assertEquals(expectedConResponse, new String(confirmString, StandardCharsets.UTF_8), false);

    }

    @Test
    @Order(11)
    public void postFreights1() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token=this.login("537300010","123456");

        byte[] responseString = mallClient.put().uri("/shops/1/orders/4000000/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n\"errno\": 504}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    @Test
    @Order(12)
    public void postFreights2() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token=this.login("537300010","123456");

        byte[] responseString = mallClient.put().uri("/shops/1/orders/1/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家对订单标记发货 订单状态为新订单不满足待发货
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(13)
    public void postFreights3() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token=this.login("537300010","123456");

        byte[] responseString = mallClient.put().uri("/shops/1/orders/48050/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":801}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家对订单标记发货 订单状态为待支付尾款
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(14)
    public void postFreights4() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token=this.login("537300010","123456");

        byte[] responseString = mallClient.put().uri("/shops/1/orders/48051/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":801}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家对订单标记发货 订单状态为待成团
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(15)
    public void postFreights5() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token=this.login("537300010","123456");

        byte[] responseString = mallClient.put().uri("/shops/1/orders/48053/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":801}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家对订单标记发货 订单状态为未成团
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(16)
    public void postFreights6() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token=this.login("537300010","123456");

        byte[] responseString = mallClient.put().uri("/shops/1/orders/48054/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":801}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家对订单标记发货 订单状态为已发货
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(17)
    public void postFreights7() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token=this.login("537300010","123456");

        byte[] responseString = mallClient.put().uri("/shops/1/orders/48055/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":801}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家对订单标记发货 订单状态为已完成
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(18)
    public void postFreights8() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token=this.login("537300010","123456");

        byte[] responseString = mallClient.put().uri("/shops/1/orders/48056/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":801}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家对订单标记发货 订单状态为已取消
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(19)
    public void postFreights9() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token=this.login("537300010","123456");

        byte[] responseString = mallClient.put().uri("/shops/1/orders/48057/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":801}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 14 店家留言测试1
     * 访问不存在的订单
     *
     * @throws Exception
     */
    @Test
    @Order(13)
    public void addShopOrderMessageTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] ret = mallClient.put()
                .uri("/shops/4567/orders/66666666")
                .header("authorization", token)
                .bodyValue("{\"message\": \"6666\"}")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 15 店家留言测试2
     * 访问不属于本商铺的订单
     *
     * @throws Exception
     */
    @Test
    @Order(14)
    public void addShopOrderMessageTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] ret = mallClient.put()
                .uri("/shops/4567/orders/2")
                .header("authorization", token)
                .bodyValue("{\"message\": \"6666\"}")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 16 店家留言测试3
     * 访问属于本商铺的订单
     *
     * @throws Exception
     */
    @Test
    @Order(15)
    public void addShopOrderMessageTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] ret = mallClient.put()
                .uri("/shops/4567/orders/240020")
                .header("authorization", token)
                .bodyValue("{\"message\": \"6666\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");

        ret = mallClient.get()
                .uri("/shops/4567/orders/240020")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":240020,\"message\":\"6666\"}}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    /**
     * 21 店家标记订单发货1
     * 访问不存在的订单
     *
     * @throws Exception
     */
    @Test
    @Order(20)
    public void markShopOrderDeliverTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] ret = mallClient.put()
                .uri("/shops/4567/orders/66666666/deliver")
                .header("authorization", token)
                .bodyValue("{\"freightSn\": \"1234567\"}")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 22 店家标记订单发货2
     * 访问不属于本商铺的订单
     *
     * @throws Exception
     */
    @Test
    @Order(21)
    public void markShopOrderDeliverTest2() throws Exception {
        String token = this.login("537300010", "123456");
        byte[] ret = mallClient.put()
                .uri("/shops/4567/orders/2/deliver")
                .header("authorization", token)
                .bodyValue("{\"freightSn\": \"1234567\"}")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 23 店家标记订单发货3
     * 访问属于本商铺的订单
     * 但当前订单状态并非“付款完成”
     *
     * @throws Exception
     */
    @Test
    @Order(22)
    public void markShopOrderDeliverTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] ret = mallClient.put()
                .uri("/shops/4567/orders/240024/deliver")
                .header("authorization", token)
                .bodyValue("{\"freightSn\": \"1234567\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");

        ret = mallClient.get()
                .uri("/shops/4567/orders/240024")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":240024,\"shipmentSn\":\"1234567\"}}";

        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 24 店家标记订单发货4
     * 访问属于本商铺的订单
     * 当前订单状态为“付款完成”
     *
     * @throws Exception
     */
    @Test
    @Order(23)
    public void markShopOrderDeliverTest4() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] ret = mallClient.put()
                .uri("/shops/4567/orders/240019/deliver")
                .header("authorization", token)
                .bodyValue("{\"freightSn\": \"1234567\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");

        ret = mallClient.get()
                .uri("/shops/4567/orders/240019")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":240019,\"shipmentSn\":\"1234567\"}}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    /**
     * 店家修改订单
     */
    @Test
    @Order(11)
    public void shopEditOrder() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"message\": \"我愛你\"\n" +
                "}";
        byte[] responseBytes = mallClient
                .put()
                .uri("/shops/7/orders/19720182203919")
                .header("authorization", token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = mallClient.get().uri("/shops/7/orders/19720182203919")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\n" +
                "  \"data\": {\n" +
                "    \"id\": 19720182203919,\n" +
                "    \"message\": \"我愛你\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家修改订单 (无权限)
     */
    @Test
    @Order(12)
    public void shopEditOrderNoRights() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"message\": \"有內鬼终止交易\"\n" +
                "}";
        byte[] responseBytes = mallClient
                .put()
                .uri("/shops/7/orders/19720182203923") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isForbidden() // 不被批准
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 店家修改订单 (不存在订单)
     * @throws Exception
     */
    @Test
    @Order(13)
    public void shopEditOrderNotExist() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"message\": \"我愛你\"\n" +
                "}";
        mallClient
                .put()
                .uri("/shops/7/orders/19720182203928") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isNotFound() // 未找到
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家修改订单 (字段不合法)
     * @throws Exception
     */
    @Test
    @Order(14)
    public void shopEditOrderFieldIllegal() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{}";
        mallClient
                .put()
                .uri("/shops/7/orders/19720182203919") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest() // 未找到
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家修改订单 (伪造 JWT)
     * @throws Exception
     */
    @Test
    @Order(15)
    public void shopEditOrderTokenIllegal() throws Exception {
        // depart = 7L
        String body = "{\n" +
                "  \"message\": \"我愛你\"\n" +
                "}";
        mallClient
                .put()
                .uri("/shops/7/orders/19720182203928") // depart=2
                .header("authorization", "12u8789781379127312ui3y1i3")
                .bodyValue(body)
                .exchange()
                .expectStatus().isUnauthorized() // 不合法
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 店家发货 (字段不合法)
     * @throws Exception
     */
    @Test
    @Order(16)
    public void shopDeliverFieldIllegal() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{}";
        mallClient
                .put()
                .uri("/shops/7/orders/19720182203919/deliver") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest() // 未找到
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 店家发货
     * @throws Exception
     */
    @Test
    @Order(17)
    public void shopDeliver() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"1212121212123\"\n" +
                "}";
        byte[] responseBytes = mallClient
                .put()
                .uri("/shops/7/orders/19720182203919/deliver")
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = mallClient.get().uri("/shops/7/orders/19720182203919")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\n" +
                "  \"data\": {\n" +
                "    \"id\": 19720182203919,\n" +
                "    \"state\": 2,\n" +
                "    \"subState\": 24,\n" +
                "    \"shipmentSn\": \"1212121212123\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家发货 (已发货过)
     * @throws Exception
     */
    @Test
    @Order(18)
    public void shopDeliverAlreadyDelivered() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"1212121212123\"\n" +
                "}";
        mallClient
                .put()
                .uri("/shops/7/orders/19720182203919/deliver")
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家发货 (状态不允许)
     * @throws Exception
     */
    @Test
    @Order(19)
    public void shopDeliverStateNotAllow() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"1212121212123\"\n" +
                "}";
        mallClient
                .put()
                .uri("/shops/7/orders/19720182203921/deliver")
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家发货 (无权限)
     */
    @Test
    @Order(20)
    public void shopDeliverNoRights() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"1212121212123\"\n" +
                "}";
        byte[] responseBytes = mallClient
                .put()
                .uri("/shops/7/orders/19720182203923/deliver") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isForbidden() // 不被批准
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家发货 (不存在订单)
     * @throws Exception
     */
    @Test
    @Order(21)
    public void shopDeliverNotExist() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"1212121212123\"\n" +
                "}";
        mallClient
                .put()
                .uri("/shops/7/orders/19720182203928/deliver") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isNotFound() // 未找到
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员登入
     * @param username 用户名
     * @param password 密码
     * @throws Exception parse error
     */
    private String adminLogin(String username, String password) {
        LoginVo vo = new LoginVo();
        vo.setUserName(username);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);
        assert requireJson != null;
        byte[] ret = manageClient.post()
                .uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        assert ret != null;
        return JacksonUtil.parseString(new String(ret, StandardCharsets.UTF_8), "data");
    }

    private String login(String userName, String password) throws Exception {
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);

        byte[] ret = manageClient.post().uri("/privileges/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }

}
