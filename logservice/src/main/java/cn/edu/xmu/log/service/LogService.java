package cn.edu.xmu.log.service;

import cn.edu.xmu.log.dao.LogDao;
import cn.edu.xmu.log.model.bo.Log;
import cn.edu.xmu.log.model.po.LogPo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    public ReturnObject<PageInfo<VoObject>> selectAllLogs(Log logInfo, Integer pageNum, Integer pageSize) {
        PageInfo<VoObject> returnObject = logDao.selectLogs(logInfo, pageNum, pageSize);

        return new ReturnObject<>(returnObject);
    }
}
