package cn.edu.xmu.order.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.model.bo.Orders;
import cn.edu.xmu.order.model.vo.AftersaleOrderVo;
import cn.edu.xmu.order.model.vo.FreightSnVo;
import cn.edu.xmu.order.model.vo.MessageVo;
import cn.edu.xmu.order.model.vo.OrderSimpleVo;
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


    /**
     * 买家修改本人名下订单
     *
     * @author 24320182203196 洪晓杰
     * @param id 订单id
     * @param vo 订单视图
     * @param bindingResult 校验数据
     * @return Object 返回视图
     */
    @ApiOperation(value = "买家修改本人名下订单", produces = "application/json")
    @ApiImplicitParams({
            //@ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "订单id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "OrderSimpleVo", name = "vo", value = "操作字段 (状态)", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    //@Audit
    @PutMapping("/orders3/{id}")
    public Object updateOrder(@PathVariable("id") Long id, @Validated @RequestBody OrderSimpleVo vo, BindingResult bindingResult) {
        logger.debug("update order by orderId:" + id);
        //校验前端数据-----暂时还没写
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        Orders orders=vo.createOrders();
        orders.setId(id);
        orders.setGmtModified(LocalDateTime.now());
        ReturnObject<Object> retObject = orderService.updateOders(orders);
        return Common.decorateReturnObject(retObject);
    }

    /**
     * 买家标记确认收货
     *
     * @author 24320182203196 洪晓杰
     * @param id 订单id
     */
    @ApiOperation(value = "买家标记确认收货", produces = "application/json")
    @ApiImplicitParams({
            //@ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "订单id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    //@Audit
    @PutMapping("/orders2/{id}/confirm")
    public Object updateOrderStateToConfirm( @PathVariable("id") Long id) {
        logger.debug("update orders by orderId:" + id);
        //校验前端数据-----暂时还没写
        Orders orders=new Orders();
        orders.setId(id);
        orders.setState((byte) 2);//2表示为确认收货状态
        orders.setGmtModified(LocalDateTime.now());
        ReturnObject<Object> retObject = orderService.updateOders(orders);
        return Common.decorateReturnObject(retObject);
    }


    /**
     * 买家取消，逻辑删除本人名下订单
     *
     * @author 24320182203196 洪晓杰
     * @param id 订单id
     */
    @ApiOperation(value = "买家取消，逻辑删除本人名下订单", produces = "application/json")
    @ApiImplicitParams({
            //@ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "订单id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    //@Audit
    @PutMapping("/orders1/{id}")
    public Object logicDeleteOrder( @PathVariable("id") Long id) {
        logger.debug("logicDelete order by orderId:" + id);
        //校验前端数据-----暂时还没写
        Orders orders=new Orders();
        orders.setId(id);
        orders.setBeDeleted((byte)1);
        orders.setGmtModified(LocalDateTime.now());
        ReturnObject<Object> retObject = orderService.updateOders(orders);
        return Common.decorateReturnObject(retObject);
    }


    /**
     * 店家修改订单 (留言)
     *
     * @author 24320182203327 张湘君
     * @param orderId 订单id
     * @param vo 留言视图
     * @param bindingResult 校验数据
     * @param userId 当前用户id
     * @return Object 订单返回视图
     */
    @ApiOperation(value = "买家修改本人名下订单。", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "订单id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "MessageVo", name = "vo", value = "可修改的留言", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @Audit
    @PutMapping("/shops/{shopId}/orders/{id}")
    public Object shopUpdateOrder(@PathVariable("id") Long orderId, @Validated @RequestBody MessageVo vo, BindingResult bindingResult,
                                  @LoginUser @ApiIgnore Long userId,
                                  @Depart @ApiIgnore Long sId,
                                  @PathVariable("shopId") Long shopId){

        logger.debug("shopUpdateOrder orderId:" + orderId);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }

        if (shopId.equals(sId)){
            Orders orders=vo.createOrder();
            orders.setId(orderId);
            orders.setShopId(shopId);
            ReturnObject<Object> retObject = orderService.shopUpdateOrder(orders);
            return Common.decorateReturnObject(retObject);
        }
        else{
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("店铺id不匹配：" + sId)), httpServletResponse);
        }

    }

    /**
     * 店家对订单标记发货
     *
     * @author 24320182203327 张湘君
     * @param orderId 订单id
     * @param userId 当前用户id
     * @return Object 订单返回视图
     */
    @ApiOperation(value = "店家对订单标记发货", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "订单id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "FreightSnVo", name = "vo", value = "", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @Audit
    @PutMapping("/shops/{shopId}/orders/{id}/deliver")
    public Object shopDeliverOrder(@PathVariable("id") Long orderId, @Validated @RequestBody FreightSnVo vo, BindingResult bindingResult,
                                   @LoginUser @ApiIgnore Long userId,
                                   @Depart @ApiIgnore Long sId,
                                   @PathVariable("shopId") Long shopId){

        logger.debug("customerConfirmOrder orderId:" + orderId);
        if(shopId.equals(sId)){
            Orders orders=vo.createOrder();
            orders.setId(orderId);
            orders.setShopId(shopId);
            ReturnObject<Object> retObject = orderService.shopDeliverOrder(orders);
            return Common.decorateReturnObject(retObject);
        }
        else{
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("店铺id不匹配：" + sId)), httpServletResponse);
        }

    }


}
