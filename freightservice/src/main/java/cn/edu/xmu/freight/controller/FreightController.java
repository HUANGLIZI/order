package cn.edu.xmu.freight.controller;

import cn.edu.xmu.freight.service.FreightService;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


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
    //@Audit
    @GetMapping("/shops/{id}/freightmodels")
    public Object getShopAllFreightModels(@PathVariable("id") Long shopId,
                                          @RequestParam(required = false) String name,
                                          @RequestParam(required = false, defaultValue = "1") Integer page,
                                          @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        logger.debug("getShopAllFreightModels: page = "+ page +"  pageSize ="+pageSize);
        ReturnObject<PageInfo<VoObject>> returnObject = freightService.getShopAllFreightModels(shopId,name,page, pageSize);
        return Common.getPageRetObject(returnObject);
    }


    /**
     * 通过id获得运费模板的概要
     *
     * @author 24320182203327 张湘君
     * @param id 运费模板id
     * @return Object 运费模板分页查询结果
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
    //@Audit
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
    //@Audit
    @PostMapping("/shops/{shopId}/freightmodels/{id}/clone")
    public Object cloneShopFreightModel(@PathVariable("shopId") Long shopId,
                                        @PathVariable("id") long id){
        logger.debug("cloneShopFreightModel: shopId="+shopId+" id="+id);

        //校验前端数据


        ReturnObject retObject=freightService.cloneShopFreightModel(shopId,id);

        if (retObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(retObject);
        } else {
            return Common.decorateReturnObject(retObject);
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
    //@Audit
    @DeleteMapping("/shops/{shopId}/freightmodels/{id}")
    public Object delShopFreightModel(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id) {
        logger.debug("delShopFreightModelById: id = "+id);

        ReturnObject returnObject = freightService.delShopFreightModel(shopId,id);
        return Common.decorateReturnObject(returnObject);
    }

}

