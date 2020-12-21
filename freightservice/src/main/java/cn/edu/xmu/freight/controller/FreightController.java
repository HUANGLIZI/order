package cn.edu.xmu.freight.controller;

import cn.edu.xmu.freight.model.vo.*;
import cn.edu.xmu.freight.model.bo.FreightModel;
import cn.edu.xmu.freight.model.bo.PieceFreightModel;
import cn.edu.xmu.freight.model.bo.WeightFreightModel;
import cn.edu.xmu.freight.model.po.FreightModelPo;
import cn.edu.xmu.freight.service.FreightService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.other.service.IAddressService;
import com.github.pagehelper.PageInfo;

import io.swagger.annotations.*;
import org.apache.dubbo.config.annotation.DubboReference;
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
@RequestMapping(value = "/order", produces = "application/json;charset=UTF-8")
public class FreightController {

    @Autowired
    private FreightService freightService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @DubboReference
    private IAddressService addressServiceI;

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
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
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
    @GetMapping("/shops/{shopId}/freightmodels/{id}")
    public Object getFreightModelById(@PathVariable("id") Long id,@PathVariable("shopId") Long shopId,@Depart @ApiIgnore Long sId){

        if(shopId!=sId&&sId!=0){
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("操作的资源id不是自己的对象")), httpServletResponse);
        }

        ReturnObject<FreightModelPo> returnObject =  freightService.getFreightModelById(id);
        if (returnObject.getCode() == ResponseCode.OK) {
            if(returnObject.getData().getShopId()!=shopId){
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()), httpServletResponse);
            }
            ReturnObject retObject=new ReturnObject(new FreightModelReturnVo(returnObject.getData()));
            return Common.getRetObject(retObject);
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
        if(shopId.equals(sId)||sId==0){
            if (retObject.getCode() == ResponseCode.OK) {
                httpServletResponse.setStatus(HttpStatus.CREATED.value());
                return Common.getRetObject(retObject);
            }else if(retObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE){
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()), httpServletResponse);
            }
            else {
                return Common.decorateReturnObject(retObject);
            }
        }
        else{
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("操作的资源id不是自己的对象")), httpServletResponse);
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
        if(shopId.equals(sId)||sId==0){

            ReturnObject returnObject = freightService.delShopFreightModel(shopId,id);
            if (returnObject.getCode() == ResponseCode.OK) {
                return Common.decorateReturnObject(returnObject);
            }else if(returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE){
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()), httpServletResponse);

            }else {
                return Common.decorateReturnObject(returnObject);
            }

        }
        else{
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("操作的资源id不是自己的对象")), httpServletResponse);
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
                                     @RequestBody FreightModelChangeVo freightModelChangeVo)
    {
        if (!shopId.equals(sId) && sId != 0) {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject<ResponseCode> ret = freightService.changeFreightModel(id, freightModelChangeVo, shopId);
        if(ret.getCode().equals(ResponseCode.OK)){
            return Common.decorateReturnObject(ret);
        }else if(ret.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE)){
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE) , httpServletResponse);
        }
        return Common.decorateReturnObject(ret);
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
        if (!shopId.equals(sId) && sId != 0) {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject<ResponseCode> ret = freightService.changeWeightFreightModel(id, weightFreightModelChangeVo, shopId);
        if(ret.getCode().equals(ResponseCode.OK)){
            return Common.decorateReturnObject(ret);
        }else if(ret.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE)){
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE) , httpServletResponse);
        }
        return Common.decorateReturnObject(ret);
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
        if (!shopId.equals(sId) && sId != 0) {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject<ResponseCode> ret = freightService.changePieceFreightModel(id, pieceFreightModelChangeVo, shopId);
        if(ret.getCode().equals(ResponseCode.OK)){
            return Common.decorateReturnObject(ret);
        }else if(ret.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE)){
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE) , httpServletResponse);
        }
        return Common.decorateReturnObject(ret);
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
            @ApiResponse(code = 805, message = "地区不可达")
    })
    @Audit
    @PostMapping(value = "/shops/{id}/freightmodels",produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object insertFreightModel(@Validated @RequestBody FreightModelVo vo, @PathVariable Long id) {
        logger.debug("insert freightmodel by shopId:" + id);
        FreightModel freightModel = vo.createFreightModel();
        freightModel.setShopId(id);
        freightModel.setDefaultModel((byte)0);
        ReturnObject<VoObject> retObject = freightService.insertFreightModel(freightModel);
        if(retObject.getCode()==ResponseCode.OK) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        }
        return Common.decorateReturnObject(retObject);
    }
    /**
     * 计算运费
     * @author 24320182203227 李子晗
     */
    @ApiOperation(value = "用户计算运费")
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
        logger.info("calculate freight service by regionId:" + rid);
        if(!addressServiceI.getValidRegionId(rid).getData()) {
            ReturnObject<Long> retObject = new ReturnObject<>(ResponseCode.PAYSN_SAME);
            return Common.decorateReturnObject(retObject);
        }
        List<Long> regionIdList=addressServiceI.getRegionId(rid).getData();
        regionIdList.add(rid);
        int listSize = vo.size();
        System.out.println(listSize);
        List<Integer> count = new ArrayList<>();
        List<Long> skuId = new ArrayList<>();
        System.out.println(rid);
        for(int i=0;i<listSize;i++)
        {
            count.add(vo.get(i).getConut());
            skuId.add(vo.get(i).getSkuId());
        }
        ReturnObject<Long> retObject =new ReturnObject<>();
        for(int i=regionIdList.size()-1;i>=0;i--) {
            retObject = freightService.calcuFreightPrice(count, skuId, regionIdList.get(i));
            if (retObject.getData()>0){
                httpServletResponse.setStatus(HttpStatus.CREATED.value());
                System.out.println(retObject.getData());
                i=-1;
            }
        }
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
    //@Audit
    @PostMapping("/shops/{shopId}/freightmodels/{id}/default")
    public Object postDefaultPieceFreight(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id){
        //Logger logger;
        logger.debug("insert role by shopId:" + shopId+"and"+id);

        ReturnObject<VoObject> returnObject =  freightService.createDefaultPieceFreight(id,shopId);

        //System.out.println(returnObject.getCode());
        if(returnObject.getCode().equals(ResponseCode.SHOP_ID_NOTEXIST)){

            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.SHOP_ID_NOTEXIST, String.format("不存在对应的shopid")), httpServletResponse);

        }else if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE)){

            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("shopid不存在对应的模板id")), httpServletResponse);
        }

        if(returnObject.getCode().equals(ResponseCode.OK))
        {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        }

        return Common.decorateReturnObject(returnObject);

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
        httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(retObject);

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

        PieceFreightModel pieceFreightModel = new PieceFreightModel(vo);

        pieceFreightModel.setFreightModelId(id);
        pieceFreightModel.setGmtCreate(LocalDateTime.now());
        pieceFreightModel.setGmtModified(LocalDateTime.now());
        pieceFreightModel.setId((long) 1);

        ReturnObject<VoObject> retObject = freightService.insertPieceFreightModel(pieceFreightModel);
        httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(retObject);

    }



    /**
     * 查询某个重量运费模板明细
     * @param shopId 店铺Id
     * @param id 重量运费模板明细id
     * @return ReturnObject
     * @author li mingming
     * @date 2020/12/12
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
    public Object findWeightItemByFreightModelId(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id, @Depart @ApiIgnore Long departId)
    {
        if(shopId == departId || departId == 0)
        {
            ReturnObject<List> returnObject = freightService.getWeightItemsByFreightModelId(shopId,id);
            //return Common.getRetObject(new ReturnObject<>(ResponseCode.OK, "22222"));
            if(returnObject.getCode() == ResponseCode.OK)
            {
                return Common.getListRetObject(returnObject);
            }
            else
            {
                return Common.decorateReturnObject(returnObject);
            }
        }
        else
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE) , httpServletResponse);
        }

    }

    /**
     * 查询某个件数运费模板明细
     * @param shopId 店铺Id
     * @param id 件数运费模板明细id
     * @return ReturnObject
     * @author li mingming
     * @date 2020/12/12
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
    public Object findPieceItemByFreightModelId(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id, @Depart @ApiIgnore Long departId)
    {
        if(shopId == departId || departId == 0)
        {
            ReturnObject<List> returnObject = freightService.getPieceItemsByFreightModelId(shopId, id);
            if (returnObject.getCode() == ResponseCode.OK) {
                return Common.getListRetObject(returnObject);
            } else {
                return Common.decorateReturnObject(returnObject);
            }
        }
        else
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE) , httpServletResponse);
        }

    }

    /**
     * 删除重量运费模板明细
     * @param id 重量运费模板明细id
     * @return ReturnObject
     * @author li mingming
     * @date 2020/12/12
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
    public Object delWeightItemById(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id,@Depart @ApiIgnore Long departId)
    {
        if(shopId == departId || departId == 0)
        {
            ReturnObject returnObject = freightService.delWeightItemById(shopId, id);
            if(returnObject.getCode().equals(ResponseCode.OK)){
                return Common.getRetObject(returnObject);
            }else if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE)){
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE) , httpServletResponse);
            }else if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)){
                return Common.decorateReturnObject(returnObject);
            }
            return Common.decorateReturnObject(returnObject);

        }
        else
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE) , httpServletResponse);
        }

    }

    /**
     * 删除件数运费模板明细
     * @param id 件数运费模板明细id
     * @return ReturnObject
     * @author li mingming
     * @date 2020/12/12
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
    public Object delPieceItemById(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id, @Depart @ApiIgnore Long departId)
    {
        if(shopId == departId || departId == 0)
        {
            ReturnObject returnObject = freightService.delPieceItemById(shopId, id);
            if(returnObject.getCode().equals(ResponseCode.OK)){
                return Common.getRetObject(returnObject);
            }else if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE)){
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE) , httpServletResponse);
            }else if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)){
                return Common.decorateReturnObject(returnObject);
            }
            return Common.getRetObject(returnObject);
        }
        else
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE) , httpServletResponse);
        }
    }


}
