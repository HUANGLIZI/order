package cn.edu.xmu.log.dao;

import cn.edu.xmu.log.mapper.LogPoMapper;
import cn.edu.xmu.log.model.bo.Log;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author Fiber W.
 * created at 11/18/20 10:36 AM
 * @detail cn.edu.xmu.log.dao
 */
@Repository
public class LogDao {
    @Autowired
    private LogPoMapper logMapper;

    public ReturnObject<VoObject> insertLog(Log log) {
        logMapper.insert(log.createPo());
        return null;
    }
}
