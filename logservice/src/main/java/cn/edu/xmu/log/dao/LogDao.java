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

    public PageInfo<LogPo> selectLogs(Integer pageNum, Integer pageSize) {
        LogPoExample example = new LogPoExample();
        LogPoExample.Criteria criteria = example.createCriteria();
//        if(logBo.getUserId() != null){
//            log.debug("selectLogs userId="+logBo.getUserId());
//            criteria.andUserIdEqualTo(logBo.getUserId());
//        }
//        if(logBo.getIp() != null){
//            log.debug("selectLogs ip="+logBo.getIp());
//            criteria.andIpEqualTo(logBo.getIp());
//        }
//        if(logBo.getPrivilegeId() != null){
//            log.debug("selectLogs privilegeId="+logBo.getPrivilegeId());
//            criteria.andPrivilegeIdEqualTo(logBo.getPrivilegeId());
//        }
//        if(logBo.getSuccess() != null){
//            log.debug("selectLogs success="+logBo.getSuccess());
//            criteria.andSuccessEqualTo(logBo.getSuccess());
//        }
//        if(logBo.getBeginDate() != null){
//            log.debug("selectLogs beginDate="+logBo.getBeginDate());
//            criteria.andGmtCreateGreaterThan(logBo.getBeginDate());
//        }
//        if(logBo.getEndDate() != null){
//            log.debug("selectLogs endDate="+logBo.getEndDate());
//            criteria.andGmtCreateLessThan(logBo.getEndDate());
//        }
        PageHelper.startPage(pageNum, pageSize);
        List<LogPo> logPos = logPoMapper.selectByExample(example);

        log.debug("selectLogs DAO:"+logPos);

        return new PageInfo<>(logPos);
    }
}
