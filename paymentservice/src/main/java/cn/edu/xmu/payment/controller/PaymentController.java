package cn.edu.xmu.payment.controller;


import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.service.PaymentService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Api(value = "支付服务", tags = "payment")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/payment", produces = "application/json;charset=UTF-8")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private HttpServletResponse httpServletResponse;


    private  static  final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    /**
     * 买家查询自己的支付信息
     *
     * @author 24320182203327 张湘君
     * @param orderId 订单id
     * @return Object 查询结果
     * createdBy 张湘君 2020/12/1 20:12
     * modifiedBy 张湘君 2020/12/1 20:12
     */
    @ApiOperation(value = "买家查询自己的支付信息",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "订单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    //@Audit
    @GetMapping("/orders/{id}/payments")
    public Object userQueryPayment(@PathVariable("id") Long orderId){
        ReturnObject returnObject =  paymentService.userQueryPayment(orderId);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getListRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
     * 管理员查询订单的支付信息
     *
     * @author 24320182203327 张湘君
     * @param orderId 订单id
     * @param shopId 店铺id
     * @return Object 查询结果
     * createdBy 张湘君 2020/12/1 20:12
     * modifiedBy 张湘君 2020/12/1 20:12
     */
    @ApiOperation(value = "管理员查询订单的支付信息",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "订单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    //@Audit
    @GetMapping("/shops/{shopId}/orders/{id}/payments")
    public Object queryPayment(@PathVariable("shopId") Long shopId,@PathVariable("id") Long orderId){
        ReturnObject returnObject =  paymentService.queryPayment(shopId,orderId);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getListRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }


}
