package cn.edu.xmu.log.dao;

import cn.edu.xmu.log.mapper.LogPoMapper;
import cn.edu.xmu.log.model.bo.Log;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author Xianwei Wang
 * created at 11/18/20 10:36 AM
 * @detail cn.edu.xmu.log.dao
 */
@Repository
public class LogDao {
    @Autowired
    private LogPoMapper logMapper;

    private static final Logger logger = LoggerFactory.getLogger(LogDao.class);
    
    /**
     * @description 插入日志
     * @param log 日志
     * @return void
     * @author Xianwei Wang
     * created at 11/18/20 11:40 AM
     */
    public void insertLog(Log log) {
        try {
            logMapper.insert(log.createPo());
        } catch (Exception e) {
            logger.error("严重错误：" + e.getMessage());
        }
    }
}
