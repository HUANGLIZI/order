package cn.edu.xmu.payment.controller;


import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.bo.Refund;
import cn.edu.xmu.payment.model.vo.PayPatternVo;
import cn.edu.xmu.payment.model.vo.PaymentVo;
import cn.edu.xmu.payment.model.vo.StateVo;
import cn.edu.xmu.payment.model.vo.amountVo;
import cn.edu.xmu.payment.service.PaymentService;
import cn.edu.xmu.payment.service.PaymentServiceI;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Api(value = "支付服务", tags = "payment")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/payment", produces = "application/json;charset=UTF-8")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentServiceI paymentServiceI;

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
    @Audit
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
    @Audit
    @GetMapping("/shops/{shopId}/orders/{id}/payments")
    public Object queryPayment(@PathVariable("shopId") Long shopId,@PathVariable("id") Long orderId){
        ReturnObject returnObject =  paymentService.queryPayment(shopId,orderId);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getListRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }




    /**
     * 买家查询自己的支付信息
     *
     * @author 24320182203196 洪晓杰
     * @param aftersaleId 售后单id
     * @return Object 查询结果
     */
    @ApiOperation(value = "买家查询自己的支付信息",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "售后单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @Audit
    @GetMapping("/aftersales/{id}/payments")
    public Object customerQueryPaymentByAftersaleId(@PathVariable("id") Long aftersaleId){
        ReturnObject returnObject =  paymentService.customerQueryPaymentByAftersaleId(aftersaleId);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getListRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }


    /**
     * 管理员查询售后单的支付信息
     *
     * @author 24320182203196 洪晓杰
     * @param aftersaleId 订单id
     * @param shopId 店铺id
     */
    @ApiOperation(value = "管理员查询售后单的支付信息",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "售后id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @Audit
    @GetMapping("/shops/{shopId}/aftersales/{id}/payments")
    public Object getPaymentByAftersaleId(@PathVariable("shopId") Long shopId,@PathVariable("id") Long aftersaleId){
        ReturnObject returnObject =  paymentService.getPaymentByAftersaleId(shopId,aftersaleId);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getListRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
     * 通过OrderId查询订单的退款信息
     *
     * @author 24320182203227 李子晗
     * @param id 订单id
     * @return Object 退款信息查询结果
     */
    @ApiOperation(value = "管理员查询订单的退款信息",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "订单id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "店铺id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    //@Audit
    @GetMapping("/shops/{shopId}/orders/{id}/refunds")
    public Object getOrdersRefundsByOrderId(@PathVariable("id") Long id,@PathVariable("shopId") Long shopId){
        ReturnObject returnObject =  paymentServiceI.getOrdersRefundsByOrderId(id,shopId);
        return returnObject;
    }
    /**
     * 通过AfterSaleId查询订单的退款信息
     *
     * @author 24320182203227 李子晗
     * @param id 订单id
     * @return Object 退款信息查询结果
     */
    @ApiOperation(value = "管理员查询订单的退款信息",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "退货单id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "店铺id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @Audit
    @GetMapping("/shops/{shopId}/aftersales/{id}/refunds")
    public Object getOrdersRefundsByAftersaleId(@PathVariable("id") Long id,@PathVariable("shopId") Long shopId){
        ReturnObject returnObject =  paymentServiceI.getOrdersRefundsByAftersaleId(id,shopId);
        return returnObject;
    }

    /**
     * 新增一个退款信息
     *
     * @author 24320182203227 李子晗
     */
    @ApiOperation(value = "管理员创建退款信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "amountVo", name = "amount", value = "金额", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "支付id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "店铺id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    //@Audit
    @PostMapping("/shops/{shopId}/payments/{id}/refunds")
    public Object insertRole(@Validated @RequestBody amountVo amount, BindingResult bindingResult,
                             @PathVariable("id") Long id, @PathVariable("shopId") Long shopId) {
        logger.debug("insert refund by paymentId:" + id+" and by shopId "+shopId);
        //校验前端数据
        Refund refund=new Refund();
        refund.setPaymentId(id);
        refund.setAmount(amount.getAmount());
        refund.setGmtCreated(LocalDateTime.now());
        ReturnObject<VoObject> retObject = paymentService.insertRefunds(refund,shopId);
        httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(retObject);
    }

    /**
     * 买家为订单创建支付单
     *
     * @author 24320182203327 张湘君
     * @param vo 支付单视图
     * @param bindingResult 校验错误
     * @param orderId 订单id
     * @return Object 角色返回视图
     * createdBy 张湘君 2020/12/3 20:12
     * modifiedBy 张湘君 2020/12/3 20:12
     */
    @ApiOperation(value = "买家为订单创建支付单", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path",dataType = "int", name="id",value = "订单id",required = true),
            @ApiImplicitParam(paramType = "body", dataType = "PaymentVo", name = "vo", value = "支付信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("/orders/{id}/payments")
    public Object createPayment(@Validated @RequestBody PaymentVo vo, BindingResult bindingResult,
                                @PathVariable("id") Long orderId){
        logger.debug("createPayment: orderId=" + orderId);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug("validate fail");
            return returnObject;
        }

        Payment payment = vo.createPayment();
        payment.setOrderId(orderId);
        payment.setGmtCreate(LocalDateTime.now());

        ReturnObject<VoObject> retObject = paymentService.createPayment(payment);
        httpServletResponse.setStatus(HttpStatus.CREATED.value());
        if (retObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(retObject);
        } else {
            return Common.decorateReturnObject(retObject);
        }

    }


    /**
     * 买家为售后单创建支付单
     *
     * @author 24320182203196  洪晓杰
     */
    @ApiOperation(value = "买家为售后单创建支付单", produces = "application/json")
    @ApiImplicitParams({
            //@ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path",dataType = "int", name="id",value = "售后单id",required = true),
            @ApiImplicitParam(paramType = "body", dataType = "PaymentVo", name = "vo", value = "支付信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    //@Audit
    @PostMapping("/aftersales/{id}/payments")
    public Object createPaymentByAftersaleId(@Validated @RequestBody PaymentVo vo, BindingResult bindingResult,
                                @PathVariable("id") Long aftersaleId){
        logger.debug("createPayment: aftersaleId=" + aftersaleId);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug("validate fail");
            return returnObject;
        }

        //需要通过aftersaleId从其他模块的aftersale表中获取orderid等信息
        Payment payment = vo.createPayment();
        payment.setAftersaleId(aftersaleId);
        payment.setGmtCreate(LocalDateTime.now());

        ReturnObject<VoObject> retObject = paymentService.createPayment(payment);
        httpServletResponse.setStatus(HttpStatus.CREATED.value());
        if (retObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(retObject);
        } else {
            return Common.decorateReturnObject(retObject);
        }

    }

    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-06 23:18
     */
    //    @Audit
    @ApiOperation(value = "买家查询自己的退款信息",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "订单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @GetMapping("/orders/{id}/refunds")
    public Object queryUserRefundsByOrderId(@LoginUser Long userId, @PathVariable("id") Long orderId) {
//        System.out.println("userId" + userId);
        ReturnObject<VoObject> returnObject = paymentServiceI.userQueryRefundsByOrderId(orderId, userId);
        return returnObject;
    }

    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-06 23:18
     */
    //    @Audit
    @ApiOperation(value = "买家查询自己的退款信息",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "支付单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @GetMapping("/aftersales/{id}/refunds")
    public Object queryUserRefundsByAftersaleId(@LoginUser Long userId, @PathVariable("id") Long aftersaleId) {
//        System.out.println("userId" + userId);
        ReturnObject<VoObject> returnObject = paymentServiceI.userQueryRefundsByAftersaleId(aftersaleId, userId);
        return returnObject;
    }



    /**
     * 获得支付单的所有状态
     *
     * @author 24320182203323 李明明
     */
    @ApiOperation(value = "获得支付单的所有状态",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    //@Audit
    @GetMapping("/states")
    public Object getAllPaymentsStates()
    {
        Payment.State[] states = Payment.State.class.getEnumConstants();
        List<StateVo> stateVos=new ArrayList<StateVo>();
        for(int i=0;i<states.length;i++){
            stateVos.add(new StateVo(states[i]));
        }
        return ResponseUtil.ok(new ReturnObject<List>(stateVos).getData());
    }

    /**
     * 获得支付渠道，目前只返回002 模拟支付渠道
     *
     * @author 24320182203323 李明明
     */
    @ApiOperation(value = "获得支付渠道，目前只返回002 模拟支付渠道",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    //@Audit
    @GetMapping("/patterns")
    public Object userQueryPayment()
    {
        List<PayPatternVo> payPatternVos=new ArrayList<PayPatternVo>();
        payPatternVos.add(new PayPatternVo("002","模拟支付渠道"));
        return ResponseUtil.ok(new ReturnObject<List>(payPatternVos).getData());
    }

}
