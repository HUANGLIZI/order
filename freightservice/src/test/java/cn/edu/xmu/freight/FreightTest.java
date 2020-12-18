package cn.edu.xmu.freight;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.privilege.model.vo.LoginVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = FreightServiceApplication.class)
@Slf4j
public class FreightTest {
    //@Value("${public-test.managementgate}")
    private String managementGate="http://localhost:8080";

    //@Value("${public-test.mallgate}")
    private String mallGate="http://localhost:8086/order";

    private WebTestClient mallClient;

    private WebTestClient manageClient;

    public FreightTest(){
        this.mallClient = WebTestClient.bindToServer()
                .baseUrl(managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.manageClient = WebTestClient.bindToServer()
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


    /**
     * 克隆运费模板 (件数)
     * @throws Exception
     */
    @Test
    @Order(5)
    public void cloneFreightModel() throws Exception {
        String token = adminLogin("shopadmin_No2", "123456");
        byte[] responseBytes = mallClient
                .post()
                .uri("/shops/7/freightmodels/284801411/clone")
                .header("authorization",token)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        assert responseBytes != null;
        String responseString = new String(responseBytes, StandardCharsets.UTF_8);
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

        // 获取定义的运费模板
        JSONObject queryResponse = JSONObject.parseObject(responseString);
        JSONObject clonedModel = queryResponse
                .getJSONObject("data");
        Long clonedFmId = clonedModel.getLong("id");

        // 查询定义的运费模板能否查出来

        byte[] queryResponseString = mallClient
                .get()
                .uri("/shops/7/freightmodels/" + clonedFmId)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 克隆运费模板 (找不到源)
     * @throws Exception
     */
    @Test
    @Order(6)
    public void cloneFreightModelNotFound() throws Exception {
        String token = adminLogin("shopadmin_No2", "123456");
        mallClient
                .post()
                .uri("/shops/7/freightmodels/2643126963/clone")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 查询商家的运费模板 (无分页)
     * @throws Exception
     */
    @Test
    @Order(8)
    public void findFreightModels() throws Exception {
        String token = adminLogin("shopadmin_No2", "123456");

        // 新建运费模板
        byte[] responseString = mallClient
                .get()
                .uri("/shops/7/freightmodels")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        assert responseString != null;
        String defString = new String(responseString, StandardCharsets.UTF_8);

        JSONObject response = JSONObject.parseObject(defString);

        Assert.isTrue(response.getString("errmsg").equals("成功"), "查询不成功");
        JSONArray modelArr = response
                .getJSONObject("data")
                .getJSONArray("list");

        boolean firstChecked = false;
        boolean secondChecked = false;
        for (int i = 0; i < modelArr.size(); i++) {
            JSONObject model = modelArr.getJSONObject(i);
            if (model.getLong("id") == 284801410L) {
                firstChecked = true;
            } else if (model.getLong("id").equals(284801411L)) {
                secondChecked = true;
            }
        }
        Assert.isTrue(firstChecked, "第一个运费模板没有找到，id=" + 284801410L);
        Assert.isTrue(secondChecked, "克隆的运费模板没有找到，id=" + 284801411L);
    }

    /**
     * 删除运费模板 (克隆源)
     * @throws Exception
     */
    @Test
    @Order(9)
    public void delFreightModel() throws Exception {
        String token = adminLogin("shopadmin_No2", "123456");
        mallClient
                .delete()
                .uri("/shops/7/freightmodels/284801411")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 删除运费模板 (找不到)
     * @throws Exception
     */
    @Test
    @Order(10)
    public void delFreightModelNotFound() throws Exception {
        String token = adminLogin("shopadmin_No2", "123456");
        mallClient
                .delete()
                .uri("/shops/7/freightmodels/5646241156151")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
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
