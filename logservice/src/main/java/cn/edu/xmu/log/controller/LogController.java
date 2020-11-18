package cn.edu.xmu.log.controller;

import cn.edu.xmu.log.model.bo.Log;
import cn.edu.xmu.log.model.vo.LogVo;
import cn.edu.xmu.log.service.LogService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 日志控制器
 * @Author 王纬策
 *
 */
@Api(value = "日志服务", tags = "privilege")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/log", produces = "application/json;charset=UTF-8")
@Slf4j
public class LogController {
    @Autowired
    private LogService logService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * @Author 王纬策
     * @date Created in 2020/11/8 0:33
     **/
    @Audit
    @ApiOperation(value = "log001: 查询日志",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageNum", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("logs/all")
    public Object selectAllLogs(
            @RequestParam(required = false, defaultValue = "1")  Integer pageNum,
            @RequestParam(required = false, defaultValue = "10")  Integer pageSize) {

        Object object = null;

        if(pageNum <= 0 || pageSize <= 0) {
            //字段不合法
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            //字段合法
            ReturnObject<PageInfo<VoObject>> returnObject = logService.selectAllLogs(pageNum, pageSize);
            log.debug("selectAllLogs: getLogs = " + returnObject);
            object = Common.getPageRetObject(returnObject);
        }

        return object;
    }
}
