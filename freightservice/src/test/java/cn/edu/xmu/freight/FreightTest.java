package cn.edu.xmu.freight;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.privilege.model.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(classes = FreightServiceApplication.class)
@Slf4j
public class FreightTest {
    //@Value("${public-test.managementgate}")
    private String managementGate="http://localhost:8083";

    //@Value("${public-test.mallgate}")
    private String mallGate="http://localhost:8080";

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    public FreightTest(){
        this.manageClient = WebTestClient.bindToServer()
                .baseUrl(managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl(mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

    }

    @Test
    public void getFreightModels() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/1/freightmodels").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        System.out.println(new String(responseString, "UTF-8"));
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"page\":1,\"pageSize\":10,\"total\":4,\"pages\":1,\"list\":[{\"id\":9,\"name\":\"测试模板\",\"type\":0,\"defaultModel\":true,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":10,\"name\":\"测试模板2\",\"type\":0,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":11,\"name\":\"测试模板3\",\"type\":0,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":12,\"name\":\"测试模板4\",\"type\":0,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"}]}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    private String login(String userName, String password) throws Exception {
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);

        byte[] ret = mallClient.post().uri("/privileges/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }

    @Test
    public void getFreightModels1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/1/freightmodels").header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        System.out.println(new String(responseString, "UTF-8"));
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    @Test
    public void getFreightModelSummary() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/freightmodels/200").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    @Test
    public void getFreightModelSummary1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/freightmodels/9").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        System.out.println(new String(responseString, "UTF-8"));
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":9,\"name\":\"测试模板\",\"type\":0,\"unit\":500,\"defaultModel\":true,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

    }

    @Test
    public void getFreightModelSummary2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/freightmodels/13").header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }


    @Test
    public void deleteFreightModel() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/freightmodels/200").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

    }

    @Test
    public void deleteFreightModel1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/freightmodels/10").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();


        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

        byte[] queryResponseString = manageClient.get().uri("/freightmodels/10").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();

        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(queryResponseString, "UTF-8"), true);

    }

    @Test
    public void deleteFreightModel2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/1/freightmodels/13").header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        String x = new String(responseString, "UTF-8");
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

    }

}
