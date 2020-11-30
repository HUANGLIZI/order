package cn.edu.xmu.freight.controller;



import cn.edu.xmu.freight.model.bo.FreightModel;
import cn.edu.xmu.freight.model.bo.PieceFreightModel;
import cn.edu.xmu.freight.model.bo.WeightFreightModel;
import cn.edu.xmu.freight.model.po.FreightModelPo;
import cn.edu.xmu.freight.model.vo.FreightModelVo;
import cn.edu.xmu.freight.model.vo.OrderItemVo;
import cn.edu.xmu.freight.model.vo.PieceFreightModelVo;
import cn.edu.xmu.freight.model.vo.WeightFreightModelVo;
import cn.edu.xmu.freight.service.FreightService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * @Author: ooad team
 * @Date: Created in
 * @Modified By:
 **/

//@Api(value = "模板服务", tags = "freight")
@RestController
@RequestMapping("")
public class FreightController {

    @Autowired
    private static FreightService freightService;

    private  static  final Logger logger = LoggerFactory.getLogger(FreightController.class);


//    @Autowired
//    RestTemplate restTemplate;

//
//    /***
//     * 赋予用户权限
//     * @param userid 用户id
//     * @param roleid 角色id
//     * @param createid 创建者id
//     * @param did 部门id
//     * @return
//     */
//    @ApiOperation(value = "赋予用户权限")
//    @ApiImplicitParams({
//            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
//            @ApiImplicitParam(name="userid", value="用户id", required = true, dataType="Integer", paramType="path"),
//            @ApiImplicitParam(name="roleid", value="角色id", required = true, dataType="Integer", paramType="path"),
//            @ApiImplicitParam(name="did", value="部门id", required = true, dataType="Integer", paramType="path")
//    })
//    @ApiResponses({
//            @ApiResponse(code = 0, message = "成功"),
//            @ApiResponse(code = 504, message = "操作id不存在")
//    })
//    @Audit
//    @PostMapping("/shops/{did}/adminusers/{userid}/roles/{roleid}")
//    public Object assignRole(@LoginUser Long createid, @PathVariable Long did, @PathVariable Long userid, @PathVariable Long roleid){
//
//        //把userService修改成freightService。
//        ReturnObject<VoObject> returnObject =  userService.assignRole(createid, userid, roleid, did);
//        if (returnObject.getCode() == ResponseCode.OK) {
//            return Common.getRetObject(returnObject);
//        } else {
//            return Common.decorateReturnObject(returnObject);
//        }
//
//    }

    /**
     * 店家或管理员为商铺定义默认运费模板。
     */
    @ApiOperation(value = "店家或管理员为商铺定义默认运费模板", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name="shopId", value="商户 ID", required = true, dataType="int", paramType="path"),
            @ApiImplicitParam(name="id", value="id", required = true, dataType="int", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("/shops/{shopId}/freight_models/{id}/default")
    public Object postDefaultPieceFreight(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id){
        //Logger logger;
        logger.debug("insert role by shopId:" + shopId+"and"+id);

        //需要写service层
        ReturnObject<VoObject> returnObject =  freightService.createDefaultPieceFreight1(id,shopId);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }


    /***
     * 管理员定义件数模板明细
     */
    @ApiOperation(value = "管理员定义件数模板明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name="shopId", value="商户 ID", required = true, dataType="int", paramType="path"),
            @ApiImplicitParam(name="id", value="id", required = true, dataType="int", paramType="path"),
            @ApiImplicitParam(name="vo", value="id", required = true, dataType="PieceFreightModelVo", paramType="body"),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("/shops/{shopId}/freightmodels/{id}/pieceItems")
    public Object postPieceFreightModel(@PathVariable Long shopId, @PathVariable Long id,@Validated @RequestBody PieceFreightModelVo vo){

        logger.debug("update role by shopId:" + shopId+"and id"+id);

        PieceFreightModel pieceFreightModel = new PieceFreightModel(vo);

        pieceFreightModel.setFreightModelId(id);
        pieceFreightModel.setGmtCreated(LocalDateTime.now());
        pieceFreightModel.setGmtModified(LocalDateTime.now());
        pieceFreightModel.setId((long) 1);
        ReturnObject<VoObject> retObject = freightService.insertPieceFreightModel(pieceFreightModel);
        //httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(retObject);

    }


    /**
     * 定义店铺的运费模板
     * @author 24320182203227 李子晗
     */

    @ApiOperation(value = "管理员定义店铺的运费模板", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "FreightModelVo", name = "vo", value = "可修改的用户信息", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "店铺id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping(value = "/shops/{id}/freightmodels",produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object insertFreightModel(@Validated @RequestBody FreightModelVo vo, @PathVariable Long id) {
        logger.debug("insert freightmodel by shopId:" + id);
        //校验前端数据
//        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
//        if (null != returnObject) {
//            logger.debug("validate fail");
//            return returnObject;
//        }
        FreightModel freightModel = vo.createFreightModel();
        freightModel.setShopId(id);
        freightModel.setGmtCreated(LocalDateTime.now());
        //System.out.println("1");
        ReturnObject<VoObject> retObject = freightService.insertFreightModel(freightModel);
        //httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(retObject);
    }
    /**
     * 计算运费
     * @author 24320182203227 李子晗
     */
    @ApiOperation(value = "管理员定义店铺的运费模板")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "OrderItemVo", name = "vo", value = "可修改的用户信息", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "rid", value = "店铺id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("/region/{rid}/price")
    @ResponseBody
    public Object calcuFreightPrice(@Validated @RequestBody List<OrderItemVo> vo, @PathVariable Long rid) {
        logger.debug("calculate freight service by regionId:" + rid);
        int listSize = vo.size();
        List<Integer> count = new ArrayList<>();
        List<String> skuId = new ArrayList<>();
        //List<Integer>count=vo.getCount();
        //List<String> skuId=vo.getSkuId();
        for(int i=0;i<listSize;i++)
        {
            count.add(vo.get(i).getConut());
            skuId.add(vo.get(i).getSkuId());
        }
        //System.out.println("1");
        //httpServletResponse.setStatus(HttpStatus.CREATED.value());
        ReturnObject<Integer> retObject = freightService.calcuFreightPrice(count,skuId);
        return Common.decorateReturnObject(retObject);
    }

    /***
     * 管理员定义管理员定义重量模板明细
     */
    @ApiOperation(value = "管理员定义重量模板明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name="shopId", value="商户 ID", required = true, dataType="int", paramType="path"),
            @ApiImplicitParam(name="id", value="id", required = true, dataType="int", paramType="path"),
            @ApiImplicitParam(name="vo", value="id", required = true, dataType="WeightFreightModelVo", paramType="body"),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("/shops/{shopId}/freightmodels/{id}/weightItems")
    public Object postWeightFreightModel(@PathVariable Long shopId, @PathVariable Long id,@Validated @RequestBody WeightFreightModelVo vo){

        logger.debug("insert WeightFreightModel by shopId:" + shopId+"and id"+id);

        WeightFreightModel weightFreightModel = new WeightFreightModel(vo);

        weightFreightModel.setFreightModelId(id);
        weightFreightModel.setGmtCreated(LocalDateTime.now());
        weightFreightModel.setGmtModified(LocalDateTime.now());
        ReturnObject<VoObject> retObject = freightService.insertWeightFreightModel(weightFreightModel);
        //httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(retObject);
    }





//    public Object insertRole(@Validated @RequestBody RoleVo vo, BindingResult bindingResult,
//                             @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
//                             @Depart @ApiIgnore @RequestParam(required = false) Long departId) {
//        logger.debug("insert role by userId:" + userId);
//        //校验前端数据
//        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
//        if (null != returnObject) {
//            logger.debug("validate fail");
//            return returnObject;
//        }
//        Role role = vo.createRole();
//        role.setCreatorId(userId);
//        role.setDepartId(departId);
//        role.setGmtCreate(LocalDateTime.now());
//        ReturnObject<VoObject> retObject = roleService.insertRole(role);
//        httpServletResponse.setStatus(HttpStatus.CREATED.value());
//        return Common.decorateReturnObject(retObject);
//    }







}
