//package cn.edu.xmu.payment.controller;
//
//import cn.edu.xmu.payment.PaymentServiceApplication;
//import cn.edu.xmu.payment.model.vo.FreightModelChangeVo;
//import cn.edu.xmu.ooad.util.JacksonUtil;
//import org.json.JSONException;
//import org.junit.jupiter.api.Test;
//import org.skyscreamer.jsonassert.JSONAssert;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
///**
// * @author Caixin
// * @date 2020-11-30 17:27
// */
//@SpringBootTest(classes = PaymentServiceApplication.class)   //标识本类是一个SpringBootTest
//@AutoConfigureMockMvc    //配置模拟的MVC，这样可以不启动服务器测试
//@Transactional
//public class CaixinFreghtControllerTest {
//
//    @Autowired
//    private MockMvc mvc;
//
//    @Test
//    public void ChangeFreightModel1()
//    {
//        FreightModelChangeVo freightModelChangeVo = new FreightModelChangeVo();
//        freightModelChangeVo.setName("testChangeModel");
//        freightModelChangeVo.setUnit(100);
//        String roleJson = JacksonUtil.toJson(freightModelChangeVo);
//        String expectedResponse = "";
//        String responseString = null;
//        try {
//            responseString = this.mvc.perform(put("/shops/1001/freightmodels/3")
//                    .contentType("application/json;charset=UTF-8").content(roleJson))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void ChangeFreightModel2()
//    {
//        FreightModelChangeVo freightModelChangeVo = new FreightModelChangeVo();
//        freightModelChangeVo.setName("pieceModel");
//        freightModelChangeVo.setUnit(100);
//        String roleJson = JacksonUtil.toJson(freightModelChangeVo);
//        String expectedResponse = "";
//        String responseString = null;
//        try {
//            responseString = this.mvc.perform(put("/shops/1001/freightmodels/3")
//                    .contentType("application/json;charset=UTF-8").content(roleJson))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        expectedResponse = "{\"errno\":802,\"errmsg\":\"运费模板名重复\"}";
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
