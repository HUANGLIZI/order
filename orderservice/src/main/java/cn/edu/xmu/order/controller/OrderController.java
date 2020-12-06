package cn.edu.xmu.order.controller;

import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.model.bo.Orders;
import cn.edu.xmu.order.model.vo.AftersaleOrderVo;
import cn.edu.xmu.order.service.OrderService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;


@Api(value = "支付服务", tags = "payment")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletResponse httpServletResponse;


    private  static  final Logger logger = LoggerFactory.getLogger(OrderController.class);

    /**
     * 通过OrderId查询订单的完整信息
     *
     * @author 24320182203227 李子晗
     * @param id 订单id
     * @return Object 订单信息查询结果
     */
    @ApiOperation(value = "买家查询订单完整信息（普通，团购，预售）",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "订单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    //@Audit
    @GetMapping("/orders/{id}")
    public Object getOrdersByOrderId(@PathVariable("id") Long id){
        ReturnObject returnObject =  orderService.getOrdersByOrderId(id);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.decorateReturnObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
     * 新增一个售后订单
     */
    @ApiOperation(value = "新增售后订单", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AftersaleOrderVo", name = "vo", value = "指定新订单的资料", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "店铺id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 900, message = "商品库存不足"),
    })
    //@Audit
    @PostMapping("/shops/{shopId}/orders")
    public Object insertAftersaleOrder(@Validated @RequestBody AftersaleOrderVo vo, BindingResult bindingResult,
                             @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                             @Depart @ApiIgnore @RequestParam(required = false) Long userShopId,
                             @PathVariable("shopId") Long shopId) {
        logger.debug("insert AfterSaleOrder by userId:" + userId);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug("validate fail");
            return returnObject;
        }
        ReturnObject<VoObject> retObject=null;
        if(userShopId==shopId) {
            Orders orders = vo.createOrder();
            orders.setShopId(shopId);
            orders.setGmtCreated(LocalDateTime.now());
            //retObject = orderService.insertOrder(orders);创建售后订单，内部API
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        }
        return Common.decorateReturnObject(retObject);
    }

    /**
     * 转换订单类型
     */
    @ApiOperation(value = "转换订单类型", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "订单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 801, message = "订单状态禁止"),
    })
    //@Audit
    @PostMapping("/orders/{id}/groupon-normal")
    public Object transOrder(@PathVariable("id") Long id,
                             @LoginUser @ApiIgnore @RequestParam(required = false) Long userId) {
        logger.debug("transform Order by orderId:" +id);
        ReturnObject<VoObject> retObject = null;
        retObject = orderService.transOrder(id);
        return Common.decorateReturnObject(retObject);
    }
}
