package cn.edu.xmu.log.controller;

import cn.edu.xmu.log.model.bo.Log;
import cn.edu.xmu.log.model.vo.LogVo;
import cn.edu.xmu.log.service.LogService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

/**
 * 日志控制器
 * @Author 王纬策
 *
 */
@Api(value = "日志服务", tags = "log")
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
    @ApiOperation(value = "log001: 查询日志",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "privilegeId", value = "权限ID", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "bool", name = "success", value = "每页数目", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "beginTime", value = "开始时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "endTime", value = "结束时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageNum", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("logs")
    @Audit
    public Object selectAllLogs(
            @LoginUser @ApiIgnore @RequestParam(required = false, defaultValue = "0") Long userId,
            @RequestParam(required = false) Long privilegeId,
            @RequestParam(required = false) Boolean success,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false, defaultValue = "1")  Integer pageNum,
            @RequestParam(required = false, defaultValue = "10")  Integer pageSize) {

        Object object = null;

        LogVo vo = new LogVo();
        if(privilegeId != null){
            vo.setPrivilegeId(privilegeId);
        }
        if(success != null){
            vo.setSuccess((byte) (success ? 0x01 : 0x00));
        }
        if(beginTime != null){
            vo.setBeginDate(beginTime);
        }
        if(endTime != null){
            vo.setEndDate(endTime);
        }
        if(userId != 0){
            vo.setUserId(userId);
        }
        Log logInfo = vo.createBo();
        ReturnObject<PageInfo<VoObject>> returnObject = logService.selectAllLogs(logInfo, pageNum, pageSize);
        log.debug("selectAllLogs: getLogs = " + returnObject);
        object = Common.getPageRetObject(returnObject);

        return object;
    }
}
