package cn.edu.xmu.log.controller;

import cn.edu.xmu.log.model.vo.LogVo;
import cn.edu.xmu.log.service.LogsService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ReturnObject;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Api(value = "日志服务", tags = "logs")
@RestController
@RequestMapping(value = "/logs", produces = "application/json;charset=UTF-8")
public class LogsControoler {

    private static final Logger logger = LoggerFactory.getLogger(LogsControoler.class);

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private LogsService logsService;

    /**
     * 清理日志
     *
     * @param departId 部门ID
     * @return Object 清空结果
     * createdBy 李狄翰 2020/11/18 10:57
     * @author 24320182203221 李狄翰
     */
    @ApiOperation(value = "log003： 清理日志", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @DeleteMapping
    public Object deleteLogs(@Validated @RequestBody LogVo vo, BindingResult bindingResult, @Depart Long departId) {
        logger.debug("delete logs");
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.info("validate fail");
            return returnObject;
        }
        ReturnObject<Object> returnObjects = logsService.deleteLogs(vo, departId);
        return Common.decorateReturnObject(returnObjects);
    }
}
