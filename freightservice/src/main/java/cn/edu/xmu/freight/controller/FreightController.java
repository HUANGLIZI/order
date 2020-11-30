package cn.edu.xmu.freight.controller;

import cn.edu.xmu.freight.model.vo.FreightModelChangeVo;
import cn.edu.xmu.freight.model.vo.PieceFreightModelChangeVo;
import cn.edu.xmu.freight.model.vo.WeightFreightModelChangeVo;
import cn.edu.xmu.freight.service.FreightService;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.annotation.Audit;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static cn.edu.xmu.ooad.util.Common.getNullRetObj;


@Api(value = "运费服务", tags = "freight")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/shops", produces = "application/json;charset=UTF-8")
public class FreightController {

    private  static  final Logger logger = LoggerFactory.getLogger(FreightController.class);

    @Autowired
    private FreightService freightService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 修改运费模板
     */
//    @Audit
    @ApiOperation(value = "修改运费模板", produces="application/json")
    @ApiImplicitParams({
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 802, message = "运费模板名重复"),
    })
    @PutMapping("{shopId}/freightmodels/{id}")
    public Object changeFreightModel(@PathVariable("shopId") Long shopId,
                                     @PathVariable("id") Long id,
                                     @Validated  @RequestBody FreightModelChangeVo freightModelChangeVo)
    {
        // 校验前端数据
//        Object returnObj = Common.processFieldErrors(bindingResult, httpServletResponse);
//        if(null != returnObj)
//        {
//            logger.debug("validate fail! shopName cannot be null!");
//            return returnObj;
//        }

        ReturnObject<Object> returnObject = freightService.changeFreightModel(id, freightModelChangeVo, shopId);
        return getNullRetObj(returnObject, httpServletResponse);
    }


//    @Audit
    @ApiOperation(value = "修改重量运费模板明细", produces="application/json")
    @ApiImplicitParams({
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 803, message = "运费模板中该地区已经定义"),
    })
    @PutMapping("{shopId}/weightItems/{id}")
    public Object changeWeightFreightModel(@PathVariable("shopId") Long shopId,
                                           @PathVariable("id") Long id,
                                           @RequestBody WeightFreightModelChangeVo weightFreightModelChangeVo)
    {
        ReturnObject<Object> objectReturnObject = freightService.changeWeightFreightModel(id, weightFreightModelChangeVo, shopId);
        return getNullRetObj(objectReturnObject, httpServletResponse);
    }

//    @Audit
    @ApiOperation(value = "修改件数运费模板", produces = "application/json")
    @ApiImplicitParams({
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 803, message = "运费模板中该地区已经定义"),
    })
    @PutMapping("{shopId}/pieceItems/{id}")
    public Object changePieceFreightModel(@PathVariable("shopId") Long shopId,
                                          @PathVariable("id") Long id,
                                          @RequestBody PieceFreightModelChangeVo pieceFreightModelChangeVo)
    {
        System.out.println(pieceFreightModelChangeVo);
        ReturnObject<Object> objectReturnObject = freightService.changePieceFreightModel(id, pieceFreightModelChangeVo, shopId);
        return getNullRetObj(objectReturnObject, httpServletResponse);
    }

}

