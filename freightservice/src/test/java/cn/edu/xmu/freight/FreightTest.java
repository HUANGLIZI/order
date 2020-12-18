package cn.edu.xmu.freight;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
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
    private String managementGate="192.168.43.194:8881";

    //@Value("${public-test.mallgate}")
    private String mallGate="192.168.43.194:8880";

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
     * 计算运费
     *
     * @throws Exception
     */
    @Test
    @Order(1)
    public void calculateFreight() throws Exception {
        String token = userLogin("8606245097", "123456");
        String json = "[{\"count\":1,\"skuId\":1275}]";
        byte[] responseString = mallClient.post().uri("/region/201/price").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":18}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }


    private String adminLogin(String userName, String password) throws Exception{
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();

        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");
    }

    private String userLogin(String userName, String password) throws Exception{
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();

        byte[] ret = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");
    }

}
