package cn.edu.xmu.order.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.model.bo.Orders;
import cn.edu.xmu.order.model.vo.*;
import cn.edu.xmu.order.service.OrderService;
import cn.edu.xmu.order.service.OrderServiceI;
import com.github.pagehelper.PageInfo;
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
import java.util.ArrayList;
import java.util.List;


@Api(value = "订单服务", tags = "order")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/order", produces = "application/json;charset=UTF-8")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderServiceI orderServiceI;

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
    @Audit
    @GetMapping("/orders/{id}")
    public Object getOrdersByOrderId(@PathVariable("id") Long id,
                                     @LoginUser Long userId){
        ReturnObject<OrderCreateRetVo> returnObject = orderService.getOrdersByOrderId(id, userId);
        if (returnObject.getCode().equals(ResponseCode.OK)) {
            return Common.decorateReturnObject(returnObject);
        } else {
            if (returnObject.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE))
            {
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                System.out.println(httpServletResponse.getStatus());
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()), httpServletResponse);
            }
            else if (returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST))
            {
                httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
                System.out.println(httpServletResponse.getStatus());
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, ResponseCode.RESOURCE_ID_NOTEXIST.getMessage()), httpServletResponse);
            }
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
    @Audit
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
    @Audit
    @PostMapping("/orders/{id}/groupon-normal")
    public Object transOrder(@PathVariable("id") Long id,
                             @LoginUser @ApiIgnore @RequestParam(required = false) Long userId)
    {
        logger.debug("transform Order by orderId:" +id);
        ReturnObject<VoObject> retObject = null;
        retObject = orderService.transOrder(id,userId);
        if(retObject.getCode()==ResponseCode.OK)
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        else if(retObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
        else if(retObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
        else if(retObject.getCode()==ResponseCode.ORDER_STATENOTALLOW)
            httpServletResponse.setStatus(HttpStatus.OK.value());
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
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "订单id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "OrderSimpleVo", name = "vo", value = "操作字段 (状态)", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("/orders/{id}")
    public Object updateOrder(@PathVariable("id") Long id,
                              @LoginUser @ApiIgnore Long userId,
                              @Validated @RequestBody OrderSimpleVo vo, BindingResult bindingResult)
    {
        logger.debug("update order by orderId:" + id);
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }

        Orders orders=vo.createOrders();
        orders.setId(id);
        orders.setGmtModified(LocalDateTime.now());
        ReturnObject<VoObject> retObject = orderService.updateOrders(orders,userId);
        if(retObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST){
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
        }
        System.out.println(retObject.getCode());
        if(retObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE){
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()), httpServletResponse);
        }
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
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "订单id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @DeleteMapping("/orders/{id}")
    public Object logicDeleteOrder( @PathVariable("id") Long id,
                                    @LoginUser @ApiIgnore Long userId
    ) {
        logger.debug("logicDelete order by orderId:" + id);
        Orders orders=new Orders();
        System.out.println(userId);
        orders.setId(id);
        orders.setGmtModified(LocalDateTime.now());
        ReturnObject<VoObject> retObject = orderService.cancelOrders(orders,userId);
        if(retObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE){
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()), httpServletResponse);
        }
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
    @ApiOperation(value = "店家修改订单 (留言)。", produces = "application/json")
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

        if (shopId.equals(sId)||sId==0){
            Orders orders=vo.createOrder();
            orders.setId(orderId);
            orders.setShopId(shopId);
            ReturnObject<Object> retObject = orderService.shopUpdateOrder(orders);
            if(retObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE){
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()), httpServletResponse);
            }
            return Common.decorateReturnObject(retObject);
        }
        else{
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()), httpServletResponse);
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

        logger.debug("shopUpdateOrder orderId:" + orderId);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        if(shopId.equals(sId)||sId==0){
            Orders orders=vo.createOrder();
            orders.setId(orderId);
            orders.setShopId(shopId);
            ReturnObject<Object> retObject = orderService.shopDeliverOrder(orders);
            if(retObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE){
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()), httpServletResponse);
            }
            return Common.decorateReturnObject(retObject);
        }
        else{
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()), httpServletResponse);
        }

    }


    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-06 12:54
     */
    @Audit
    @ApiOperation(value="获得订单的所有状态")
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @GetMapping("/orders/states")
    public Object getOrderState()
    {
        Orders.State[] states=Orders.State.class.getEnumConstants();
        List<StateVo> stateVos=new ArrayList<StateVo>();
        for(int i=0;i<states.length;i++){
            stateVos.add(new StateVo(states[i]));
        }
        return ResponseUtil.ok(new ReturnObject<List>(stateVos).getData());
    }


    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-06 21:15
     */
    @Audit
    @ApiOperation(value = "查询用户订单", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "orderSn", value = "订单编号", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Byte", name = "state", value = "订单状态", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "LocalDateTime", name = "beginTime", value = "开始时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "LocalDateTime", name = "endTime", value = "结束时间", required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/orders")
    public Object selectAllOrders(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderSn,
            @RequestParam(required = false) Byte state,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime) {
        logger.debug("selectAllRoles: page = " + page + "  pageSize =" + pageSize);
        ReturnObject<PageInfo<VoObject>> returnObject = orderService.selectOrders(userId, page, pageSize, orderSn, state, beginTime, endTime);
        if (returnObject.getCode() == ResponseCode.FIELD_NOTVALID)
        {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID,ResponseCode.FIELD_NOTVALID.getMessage()),httpServletResponse);

        }
        else if (returnObject.getCode() == ResponseCode.OK)
            return Common.getPageRetObject(returnObject);
        else {
            return Common.decorateReturnObject(returnObject);
        }
    }


    /**
     * 用户创建订单
     * @author Cai Xinlu
     * @date 2020-12-10 10:46
     */
    @Audit
    @ApiOperation(value = "用户创建订单", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "OrdersVo", name = "ordersVo", value = "创建订单信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @PostMapping("/orders")
    public Object createOrders(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
            @Validated @RequestBody OrdersVo ordersVo) {
        if (ordersVo.getCouponId() != null && ordersVo.getGrouponId() != null)
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
        if (ordersVo.getPresaleId() != null && ordersVo.getGrouponId() != null)
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
        if (ordersVo.getPresaleId() != null && ordersVo.getCouponId() != null)
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
        ReturnObject orders = orderServiceI.createOrders(userId, ordersVo);
        if (orders.getCode().equals(ResponseCode.OK))
        {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.decorateReturnObject(orders);
        }
        else
            return Common.decorateReturnObject(orders);
    }


    /**
     * 店家查询商户所有订单 (概要)
     *
     * @author 24320182203323  李明明
     * @param page 页数
     * @param pageSize 每页大小
     * @return Object 查询结果
     * @date 2020/12/12
     */
    @ApiOperation(value = "店家查询商户所有订单 (概要)",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "customerId", value = "购买者用户id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "orderSn", value = "订单Sn", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "beginTime", value = "开始时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "endTime", value = "结束时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @GetMapping("/shops/{shopId}/orders")
    public Object getShopAllFreightModels(@PathVariable("shopId") Long shopId,
                                          @Depart @ApiIgnore Long departId,
                                          @RequestParam(required = false) Long customerId,
                                          @RequestParam(required = false) String orderSn,
                                          @RequestParam(required = false) String beginTime,
                                          @RequestParam(required = false) String endTime,
                                          @RequestParam(required = false, defaultValue = "1") Integer page,
                                          @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        logger.debug("getShopAllFreightModels: page = "+ page +"  pageSize ="+pageSize);
        if(shopId.equals(departId) || departId.equals(0L))
        {
            ReturnObject<PageInfo<VoObject>> returnObject = orderService.getShopAllOrders(shopId,customerId, orderSn, beginTime, endTime, page, pageSize);
            return Common.getPageRetObject(returnObject);
        }
        else
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE), httpServletResponse);
        }
    }

    /**
     * 店家查询店内订单完整信息（普通，团购，预售）
     *
     * @author 24320182203323  李明明
     * @return Object 查询结果
     * @date 2020/12/12
     */
    @ApiOperation(value = "店家查询店内订单完整信息（普通，团购，预售）",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "商户id (店员只能查询本商铺)", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "订单id", required = true)

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @Audit
    @GetMapping("/shops/{shopId}/orders/{id}")
    public Object getOrderById(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id, @Depart @ApiIgnore Long departId){

        if(shopId.equals(departId)  || departId.equals(0L))
        {
            ReturnObject returnObject =  orderService.getOrderById(shopId, id);
            if (returnObject.getCode() == ResponseCode.OK) {
                return Common.decorateReturnObject(returnObject);
            }
            else if(returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
            {
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.decorateReturnObject(returnObject);
            }
            else if(returnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
            {
                httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
                return Common.decorateReturnObject(returnObject);
            }
            else {
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.decorateReturnObject(returnObject);
            }
        }
        else
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE), httpServletResponse);
        }
    }


    /**
     * 管理员取消本店铺订单
     *
     * @author 24320182203323  李明明
     * @return Object 查询结果
     * @date 2020/12/12
     */
    @ApiOperation(value = "管理员取消本店铺订单",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "商户id (店员只能查询本商铺)", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "订单id", required = true)

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/orders/{id}")
    public Object cancelOrderById(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id, @Depart @ApiIgnore Long departId)
    {
        if(shopId.equals(departId) || departId.equals(0L))
        {
            logger.debug("Cancel Order by orderId:" +id);
            ReturnObject<VoObject> returnObject = orderService.cancelOrderById(shopId, id);
            if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE))
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            if(returnObject.getCode().equals(ResponseCode.ORDER_STATENOTALLOW))
                httpServletResponse.setStatus(HttpStatus.OK.value());
            return Common.decorateReturnObject(returnObject);
        }
        else
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE), httpServletResponse);
        }

    }

    /**
     * 买家标记确认收货
     *
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
    @Audit
    @PutMapping("/orders/{id}/confirm")
    public Object updateOrderStateToConfirm( @PathVariable("id") Long id,
                                             @LoginUser Long userId) {
        logger.debug("update orders by orderId:" + id);
        ReturnObject<ResponseCode> returnObject = orderService.userConfirmState(userId, id);
        if (returnObject.getCode().equals(ResponseCode.OK))
        {
            return Common.decorateReturnObject(returnObject);
        }
        else if (returnObject.getCode().equals(ResponseCode.ORDER_STATENOTALLOW))
        {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW,ResponseCode.ORDER_STATENOTALLOW.getMessage()),httpServletResponse);
        }
        else if (returnObject.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE))
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()),httpServletResponse);
        }
        return Common.decorateReturnObject(returnObject);
    }
}
