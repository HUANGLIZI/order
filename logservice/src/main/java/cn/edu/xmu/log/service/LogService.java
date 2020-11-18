package cn.edu.xmu.log.service;

import cn.edu.xmu.log.Controller.LogsController;
import cn.edu.xmu.log.dao.LogDao;
import cn.edu.xmu.log.model.bo.Log;
import cn.edu.xmu.log.model.po.LogPo;
import cn.edu.xmu.log.model.vo.LogVo;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 日志服务类
 *
 * @author 24320182203281 王纬策
 * createdBy 王纬策 2020/11/04 13:57
 * modifiedBy 王纬策 2020/11/7 19:20
 **/
@Service
public class LogService {
    @Autowired
    private LogDao logDao;

    /**
     * 根据条件分页查询日志
     *
     * @author 24320182203281 王纬策
     * @param pageNum  页数
     * @param pageSize 每页大小
     * @return ReturnObject<PageInfo < VoObject>> 分页返回日志信息
     * @date Created in 2020/11/18 10:33
     * @date Modified in 2020/11/18 19:32
     */
    public ReturnObject<PageInfo<VoObject>> selectAllLogs(Log logInfo, Integer pageNum, Integer pageSize) {
        PageInfo<VoObject> returnObject = logDao.selectLogs(logInfo, pageNum, pageSize);

        return new ReturnObject<>(returnObject);
    }

    private static final Logger logger = LoggerFactory.getLogger(LogsController.class);

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
