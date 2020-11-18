package cn.edu.xmu.log.dao;

import cn.edu.xmu.log.mapper.LogPoMapper;
import cn.edu.xmu.log.model.bo.Log;
import cn.edu.xmu.log.model.po.LogPo;
import cn.edu.xmu.log.model.po.LogPoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 日志服务类
 *
 * @author 24320182203281 王纬策
 * createdBy 王纬策 2020/11/04 13:57
 * modifiedBy 王纬策 2020/11/7 19:20
 **/
@Slf4j
@Repository
public class LogDao {
    @Autowired
    private LogPoMapper logPoMapper;

    public PageInfo<LogPo> selectLogs(Log logInfo, Integer pageNum, Integer pageSize) {
        LogPoExample example = new LogPoExample();
        LogPoExample.Criteria criteria = example.createCriteria();
        if(logInfo.getUserId() != null){
            log.debug("selectLogs userId="+logInfo.getUserId());
            criteria.andUserIdEqualTo(logInfo.getUserId());
        }
        if(logInfo.getIp() != null){
            log.debug("selectLogs ip="+logInfo.getIp());
            criteria.andIpEqualTo(logInfo.getIp());
        }
        if(logInfo.getPrivilegeId() != null){
            log.debug("selectLogs privilegeId="+logInfo.getPrivilegeId());
            criteria.andPrivilegeIdEqualTo(logInfo.getPrivilegeId());
        }
        if(logInfo.getSuccess() != null){
            log.debug("selectLogs success="+logInfo.getSuccess());
            criteria.andSuccessEqualTo(logInfo.getSuccess());
        }
        if(logInfo.getBeginDate() != null){
            log.debug("selectLogs beginDate="+logInfo.getBeginDate());
            criteria.andGmtCreateGreaterThan(logInfo.getBeginDate());
        }
        if(logInfo.getEndDate() != null){
            log.debug("selectLogs endDate="+logInfo.getEndDate());
            criteria.andGmtCreateLessThan(logInfo.getEndDate());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<LogPo> logPos = logPoMapper.selectByExample(example);

        log.debug("selectLogs DAO:"+logPos);

        return new PageInfo<>(logPos);
    }
}
