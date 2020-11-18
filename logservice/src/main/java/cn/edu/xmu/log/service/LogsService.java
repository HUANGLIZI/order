package cn.edu.xmu.log.service;

import cn.edu.xmu.log.dao.LogsDao;
import cn.edu.xmu.log.model.bo.Log;
import cn.edu.xmu.log.model.vo.LogVo;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 日志服务类
 *
 * @author 24320182203221 李狄翰
 * createdBy 李狄翰 2020/11/18 10:57
 **/
@Service
public class LogsService {
    @Autowired
    LogsDao logsDao;

    /**
     * 清理日志
     *
     * @param departId 部门ID
     * @return ReturnObject<Object> 返回视图
     * createdBy 李狄翰 2020/11/18 10:57
     * @author 24320182203221 李狄翰
     */
    @Transactional
    public ReturnObject<Object> deleteLogs(LogVo vo, Long departId) {
        Log log = new Log(vo);
        return logsDao.deleteLogs(log, departId);
    }

}
