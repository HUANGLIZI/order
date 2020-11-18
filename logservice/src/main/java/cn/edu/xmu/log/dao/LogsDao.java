package cn.edu.xmu.log.dao;

import cn.edu.xmu.log.mapper.LogPoMapper;
import cn.edu.xmu.log.model.bo.Log;
import cn.edu.xmu.log.model.po.LogPoExample;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Objects;

/**
 * 角色访问类
 *
 * @author 24320182203221 李狄翰
 * createdBy 李狄翰 2020/11/18 10:57
 **/
@Repository
public class LogsDao implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(LogsDao.class);

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private LogPoMapper logMapper;


    /**
     * 清空日志
     *
     * @param departId 部门ID
     * @return ReturnObject<Object> 删除结果
     * createdBy 李狄翰 2020/11/18 10:57
     * @author 24320182203221 李狄翰
     */
    public ReturnObject<Object> deleteLogs(Log log, Long departId) {
        logger.debug("deleteLogs");
        ReturnObject<Object> retObj = new ReturnObject<>();
        LogPoExample example = new LogPoExample();
        LogPoExample.Criteria criteria = example.createCriteria();
        criteria.andGmtCreateBetween(log.getBeginTime(), log.getEndTime());
        //判断是否为管理员
        if (!Objects.equals(departId, 0L)) {
            criteria.andDepartIdEqualTo(departId);
        }
        logMapper.deleteByExample(example);
        return retObj;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
