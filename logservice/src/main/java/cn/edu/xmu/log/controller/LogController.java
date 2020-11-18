package cn.edu.xmu.log.controller;

import cn.edu.xmu.log.model.bo.Log;
import cn.edu.xmu.log.model.vo.LogVo;
import cn.edu.xmu.log.service.LogService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
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
     * 查询日志
     *
     * @param userId 用户id
     * @param departId 部门id
     * @param privilegeId 权限id
     * @param success 是否成功
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param pageNum 页码
     * @param pageSize 页面大小
     *
     * @author 王纬策
     * @date Created in 2020/11/18 0:33
     * @date Modified in 2020/11/18 19:32
     **/
    @Audit
    @ApiOperation(value = "log001: 查询日志",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "did", value = "部门ID", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "userId", value = "用户ID", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "privilegeId", value = "权限ID", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "bool", name = "success", value = "是否成功", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "beginTime", value = "开始时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "endTime", value = "结束时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageNum", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/shops/{did}/logs")
    public Object selectAllLogs(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long uid,
            @Depart @ApiIgnore @RequestParam(required = false) Long departId,
            @PathVariable("did") Long did,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long privilegeId,
            @RequestParam(required = false) Boolean success,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false, defaultValue = "1")  Integer pageNum,
            @RequestParam(required = false, defaultValue = "10")  Integer pageSize) {
        if(departId == 0L || did.equals(departId)){
            Object object = null;

            LogVo vo = new LogVo();
            vo.setDepartId(did);
            if(userId != null){
                vo.setUserId(userId);
            }
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
            Log logInfo = vo.createBo();
            ReturnObject<PageInfo<VoObject>> returnObject = logService.selectAllLogs(logInfo, pageNum, pageSize);
            log.debug("selectAllLogs: getLogs = " + returnObject);
            object = Common.getPageRetObject(returnObject);

            return object;
        }
        else{
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("部门id不匹配：" + did)), httpServletResponse);
        }

    }
}
