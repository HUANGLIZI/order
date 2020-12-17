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
    private String managementGate="http://localhost:8080";

    //@Value("${public-test.mallgate}")
    private String mallGate="http://localhost:8086";

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

    /**
     * 测试获取模板概要功能
     * 操作的资源id不存在
     *
     * @throws Exception
     */
    @Test
    public void getFreightModelSummary() throws Exception {
        String token = login("537300010", "123456");
        byte[] responseString = mallClient.get().uri("/shops/1/freightmodels/200").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 测试获取模板概要功能
     * 成功
     *
     * @throws Exception
     */
    @Test
    public void getFreightModelSummary1() throws Exception {
        String token = login("537300010", "123456");
        byte[] responseString = mallClient.get().uri("/shops/1/freightmodels/9").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":9,\"name\":\"测试模板\",\"type\":0,\"unit\":500,\"defaultModel\":true,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

    }

    /**
     * 测试获取模板概要功能
     * 操作的资源id不是自己的对象
     *
     * @throws Exception
     */
    @Test
    public void getFreightModelSummary2() throws Exception {
        String token = login("537300010", "123456");
        byte[] responseString = mallClient.get().uri("/shops/1/freightmodels/13").header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }


    /**
     * 测试获取运费模板功能
     * 全部获取
     *
     * @throws Exception
     */
    @Test
    public void getFreightModels() throws Exception {
        String token = login("537300010", "123456");
        byte[] responseString = mallClient.get().uri("/shops/1/freightmodels").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"page\":1,\"pageSize\":10,\"total\":6,\"pages\":1,\"list\":[{\"id\":9,\"name\":\"测试模板\",\"type\":0,\"unit\":500,\"defaultModel\":true,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":10,\"name\":\"测试模板2\",\"type\":0,\"unit\":500,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":11,\"name\":\"测试模板3\",\"type\":0,\"unit\":500,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":12,\"name\":\"测试模板4\",\"type\":0,\"unit\":500,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":22,\"name\":\"ight model/100g\",\"type\":0,\"unit\":100,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":23,\"name\":\"piece model/2\",\"type\":1,\"unit\":2,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"}]}}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }


    /**
     * 测试获取运费模板功能
     * 按名字获取
     *
     * @throws Exception
     */
    @Test
    public void getFreightModels1() throws Exception {
        String token = login("537300010", "123456");
        byte[] responseString = mallClient.get().uri("/shops/1/freightmodels?name=测试模板4").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"page\":1,\"pageSize\":10,\"total\":1,\"pages\":1,\"list\":[{\"id\":12,\"name\":\"测试模板4\",\"type\":0,\"unit\":500,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"}]}}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 测试获取运费模板功能
     * 指定页大小
     *
     * @throws Exception
     */
    @Test
    public void getFreightModels2() throws Exception {
        String token = login("537300010", "123456");
        byte[] responseString = mallClient.get().uri("/shops/1/freightmodels?pageSize=2").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        System.out.println(new String(responseString, "UTF-8"));

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"page\":1,\"pageSize\":2,\"total\":6,\"pages\":3,\"list\":[{\"id\":9,\"name\":\"测试模板\",\"type\":0,\"unit\":500,\"defaultModel\":true,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":10,\"name\":\"测试模板2\",\"type\":0,\"unit\":500,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"}]}}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 测试获取运费模板功能
     * 指定页大小和页数
     *
     * @throws Exception
     */
    @Test
    public void getFreightModels3() throws Exception {
        String token = login("537300010", "123456");
        byte[] responseString = mallClient.get().uri("/shops/1/freightmodels?pageSize=2&page=2").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        System.out.println(new String(responseString, "UTF-8"));

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"page\":2,\"pageSize\":2,\"total\":6,\"pages\":3,\"list\":[{\"id\":11,\"name\":\"测试模板3\",\"type\":0,\"unit\":500,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":12,\"name\":\"测试模板4\",\"type\":0,\"unit\":500,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"}]}}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
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
