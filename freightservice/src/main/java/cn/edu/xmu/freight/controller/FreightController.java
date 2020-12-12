package cn.edu.xmu.freight.controller;

import cn.edu.xmu.freight.model.vo.FreightModelChangeVo;
import cn.edu.xmu.freight.model.vo.PieceFreightModelChangeVo;
import cn.edu.xmu.freight.model.vo.WeightFreightModelChangeVo;
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
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import static cn.edu.xmu.ooad.util.Common.getNullRetObj;


@Api(value = "运费服务", tags = "freight")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/freight", produces = "application/json;charset=UTF-8")
public class FreightController {

    @Autowired
    private FreightService freightService;

    @Autowired
    private HttpServletResponse httpServletResponse;


    private  static  final Logger logger = LoggerFactory.getLogger(FreightController.class);

    /**
     * 分页查询店铺的所有运费模板
     *
     * @author 24320182203327 张湘君
     * @param page 页数
     * @param pageSize 每页大小
     * @return Object 运费模板分页查询结果
     * createdBy 张湘君 2020/11/25 20:12
     * modifiedBy 张湘君 2020/11/25 20:12
     */
    @ApiOperation(value = "查询运费模板",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "name", value = "模板名称", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false)
        })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @GetMapping("/shops/{id}/freightmodels")
    public Object getShopAllFreightModels(@PathVariable("id") Long shopId,
                                          @Depart @ApiIgnore Long sId,
                                          @RequestParam(required = false) String name,
                                          @RequestParam(required = false, defaultValue = "1") Integer page,
                                          @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        logger.debug("getShopAllFreightModels: page = "+ page +"  pageSize ="+pageSize);
        if(shopId.equals(sId)||sId==0){
            ReturnObject<PageInfo<VoObject>> returnObject = freightService.getShopAllFreightModels(shopId,name,page, pageSize);
            return Common.getPageRetObject(returnObject);
        }else{
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("操作的资源id不是自己的对象")), httpServletResponse);
        }

    }


    /**
     * 通过id获得运费模板的概要
     *
     * @author 24320182203327 张湘君
     * @param id 运费模板id
     * @return Object 运费模板查询结果
     * createdBy 张湘君 2020/11/25 20:12
     * modifiedBy 张湘君 2020/11/25 20:12
     */
    @ApiOperation(value = "通过id获得运费模板的概要",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "模板id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @Audit
    @GetMapping("/freightmodels/{id}")
    public Object getFreightModelById(@PathVariable("id") Long id){


        ReturnObject returnObject =  freightService.getFreightModelById(id);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
     * 管理员克隆店铺的运费模板。
     *
     * @author 24320182203327 张湘君
     * @param shopId 店铺rid
     * @param id 模板id
     * @return Object 返回clone出来的对象
     * createdBy 张湘君 2020/11/27 20:12
     * modifiedBy 张湘君 2020/11/27 20:12
     */
    @ApiOperation(value = "管理员克隆店铺的运费模板",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path",dataType = "int",name = "id",value = "模板id",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @Audit
    @PostMapping("/shops/{shopId}/freightmodels/{id}/clone")
    public Object cloneShopFreightModel(@PathVariable("shopId") Long shopId,
                                        @Depart @ApiIgnore Long sId,
                                        @PathVariable("id") long id){
        logger.debug("cloneShopFreightModel: shopId="+shopId+" id="+id);

        //校验前端数据


        ReturnObject retObject=freightService.cloneShopFreightModel(shopId,id);
        if(shopId.equals(sId)){
            if (retObject.getCode() == ResponseCode.OK) {
                return Common.getRetObject(retObject);
            } else {
                return Common.decorateReturnObject(retObject);
            }
        }
        else{
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("店铺id不匹配：" + sId)), httpServletResponse);
        }

    }

    /**
     * 删除运费模板，需同步删除与商品的
     *
     * @author 24320182203327 张湘君
     * @param shopId 店铺rid
     * @param id 模板id
     * @return Object 删除结果
     * createdBy 张湘君 2020/11/28 20:12
     * modifiedBy 张湘君 2020/11/28 20:12
     */
    @ApiOperation(value = "删除运费模板", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "运费模板id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/freightmodels/{id}")
    public Object delShopFreightModel(@PathVariable("shopId") Long shopId, @Depart @ApiIgnore Long sId,@PathVariable("id") Long id) {
        logger.debug("delShopFreightModelById: id = "+id);
        if(shopId.equals(sId)){
            ReturnObject returnObject = freightService.delShopFreightModel(shopId,id);
            return Common.decorateReturnObject(returnObject);
        }
        else{
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("店铺id不匹配：" + sId)), httpServletResponse);
        }

    }

    /**
     * 修改运费模板
     * @author Cai Xinlu
     * @date 2020-12-10 9:40
     */
    @Audit
    @ApiOperation(value = "修改运费模板", produces="application/json")
    @ApiImplicitParams({
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 802, message = "运费模板名重复"),
    })
    @PutMapping("/shops/{shopId}/freightmodels/{id}")
    public Object changeFreightModel(@Depart @ApiIgnore Long sId,
                                     @PathVariable("shopId") Long shopId,
                                     @PathVariable("id") Long id,
                                     @Validated  @RequestBody FreightModelChangeVo freightModelChangeVo)
    {

        ReturnObject<Object> returnObject = freightService.changeFreightModel(id, freightModelChangeVo, shopId, sId);
        return getNullRetObj(returnObject, httpServletResponse);
    }


    /**
     * 修改重量运费模板明细
     * @author Cai Xinlu
     * @date 2020-12-10 9:40
     */
    @Audit
    @ApiOperation(value = "修改重量运费模板明细", produces="application/json")
    @ApiImplicitParams({
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 803, message = "运费模板中该地区已经定义"),
    })
    @PutMapping("/shops/{shopId}/weightItems/{id}")
    public Object changeWeightFreightModel(@Depart @ApiIgnore Long sId,
                                           @PathVariable("shopId") Long shopId,
                                           @PathVariable("id") Long id,
                                           @RequestBody WeightFreightModelChangeVo weightFreightModelChangeVo)
    {
        ReturnObject<Object> objectReturnObject = freightService.changeWeightFreightModel(id, weightFreightModelChangeVo, shopId, sId);
        return getNullRetObj(objectReturnObject, httpServletResponse);
    }

    /**
     * 修改件数运费模板
     * @author Cai Xinlu
     * @date 2020-12-10 9:40
     */
    @Audit
    @ApiOperation(value = "修改件数运费模板", produces = "application/json")
    @ApiImplicitParams({
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 803, message = "运费模板中该地区已经定义"),
    })
    @PutMapping("/shops/{shopId}/pieceItems/{id}")
    public Object changePieceFreightModel(@Depart @ApiIgnore Long sId,
                                          @PathVariable("shopId") Long shopId,
                                          @PathVariable("id") Long id,
                                          @RequestBody PieceFreightModelChangeVo pieceFreightModelChangeVo)
    {
        System.out.println(pieceFreightModelChangeVo);
        ReturnObject<Object> objectReturnObject = freightService.changePieceFreightModel(id, pieceFreightModelChangeVo, shopId, sId);
        return getNullRetObj(objectReturnObject, httpServletResponse);
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
        FreightModel freightModel = vo.createFreightModel();
        freightModel.setShopId(id);
        freightModel.setGmtCreate(LocalDateTime.now());
        ReturnObject<VoObject> retObject = freightService.insertFreightModel(freightModel);
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
        List<Long> skuId = new ArrayList<>();

        for(int i=0;i<listSize;i++)
        {
            count.add(vo.get(i).getConut());
            skuId.add(vo.get(i).getSkuId());
        }
        ReturnObject<Long> retObject = freightService.calcuFreightPrice(count,skuId,rid);
        return Common.decorateReturnObject(retObject);
    }

    /**
     * @Author:洪晓杰
     * 店家或管理员为商铺定义默认运费模板。
     */
    @ApiOperation(value = "店家或管理员为商铺定义默认运费模板", produces = "application/json")
    @ApiImplicitParams({
            //@ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
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
        ReturnObject<VoObject> returnObject =  freightService.createDefaultPieceFreight(id,shopId);

        return Common.decorateReturnObject(returnObject);

//        if (returnObject.getCode() == ResponseCode.OK) {
//            return Common.getRetObject(returnObject);
//        } else {
//            return Common.decorateReturnObject(returnObject);
//        }
    }


    /***
     * @Author:洪晓杰
     * 管理员定义件数模板明细
     */
    @ApiOperation(value = "管理员定义件数模板明细")
    @ApiImplicitParams({
            //@ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
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

        //校验前端数据
        //Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        PieceFreightModel pieceFreightModel = new PieceFreightModel(vo);
//        if (null != returnObject) {
//            logger.debug("validate fail");
//            return returnObject;
//        }
        pieceFreightModel.setFreightModelId(id);
        pieceFreightModel.setGmtCreate(LocalDateTime.now());
        pieceFreightModel.setGmtModified(LocalDateTime.now());
        pieceFreightModel.setId((long) 1);
        ReturnObject<VoObject> retObject = freightService.insertPieceFreightModel(pieceFreightModel);
        //httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(retObject);

    }


    /***
     * @Author:洪晓杰
     * 管理员定义管理员定义重量模板明细
     */
    @ApiOperation(value = "管理员定义重量模板明细")
    @ApiImplicitParams({
            //@ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
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
        weightFreightModel.setGmtCreate(LocalDateTime.now());
        weightFreightModel.setGmtModified(LocalDateTime.now());
        ReturnObject<VoObject> retObject = freightService.insertWeightFreightModel(weightFreightModel);
        //httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(retObject);

    }


    /**
     * 查询某个重量运费模板明细
     * @param shopId 店铺Id
     * @param id 重量运费模板明细id
     * @return ReturnObject
     * @author li mingming
     */
    @ApiOperation(value = "查询某个重量运费模板明细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺Id", required = true, dataType="int", paramType="path"),
            @ApiImplicitParam(name="id", value="运费模板id", required = true, dataType="int", paramType="path")

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @Audit
    @GetMapping("/shops/{shopId}/freightmodels/{id}/weightItems")
    public Object findWeightItemByFreightModelId(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id)
    {
        ReturnObject<List> returnObject = freightService.getWeightItemsByFreightModelId(shopId,id);
        if(returnObject.getCode() == ResponseCode.OK)
        {
            return Common.getListRetObject(returnObject);
        }
        else
        {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
     * 查询某个件数运费模板明细
     * @param shopId 店铺Id
     * @param id 件数运费模板明细id
     * @return ReturnObject
     * @author li mingming
     */
    @ApiOperation(value = "查询某个件数运费模板明细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺Id", required = true, dataType="int", paramType="path"),
            @ApiImplicitParam(name="id", value="运费模板id", required = true, dataType="int", paramType="path")

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @Audit
    @GetMapping("/shops/{shopId}/freightmodels/{id}/pieceItems")
    public Object findPieceItemByFreightModelId(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id)
    {
        ReturnObject<List> returnObject = freightService.getPieceItemsByFreightModelId(shopId, id);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getListRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
     * 删除重量运费模板明细
     * @param id 重量运费模板明细id
     * @return ReturnObject
     * @author li mingming
     */
    @ApiOperation(value = "删除重量运费模板明细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", value="重量运费模板明细id", required = true, dataType="int", paramType="path"),
            @ApiImplicitParam(name="shopId", value="店铺Id", required = true, dataType="int", paramType="path"),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/weightItems/{id}")
    public Object delWeightItemById(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id)
    {
        ReturnObject<VoObject> returnObject = freightService.delWeightItemById(shopId, id);
        return Common.getRetObject(returnObject);
    }

    /**
     * 删除件数运费模板明细
     * @param id 件数运费模板明细id
     * @return ReturnObject
     * @author li mingming
     */
    @ApiOperation(value = "删除件数运费模板明细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", value="件数运费模板明细id", required = true, dataType="int", paramType="path"),
            @ApiImplicitParam(name="shopId", value="店铺Id", required = true, dataType="int", paramType="path"),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/pieceItems/{id}")
    public Object delPieceItemById(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id)
    {
        ReturnObject<VoObject> returnObject = freightService.delPieceItemById(shopId, id);
        return Common.getRetObject(returnObject);
    }



}
