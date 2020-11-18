package cn.edu.xmu.log.model.bo;

import cn.edu.xmu.log.model.po.LogPo;
import cn.edu.xmu.log.model.vo.LogVo;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 用户代理Bo类
 *
 * @author 24320182203221 李狄翰
 * createdBy 李狄翰2020/11/09 12:00
 **/
@Data
public class Log {
    private Long id;
    private Long userId;
    private Long privilegeId;
    private String ip;
    private String descr;
    private Byte success;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private LocalDateTime gmtCreate;

    public Log() {
    }

    public Log(LogVo vo) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.beginTime = LocalDateTime.parse(vo.getBeginTime(), df);
        this.endTime = LocalDateTime.parse(vo.getEndTime(), df);

    }

    /**
     * 构造函数
     *

     */
    public Log(LogPo po) {
        this.id = po.getId();
        this.userId = po.getUserId();
        this.privilegeId = po.getPrivilegeId();
        this.ip = po.getIp();
        this.descr = po.getDescr();
        this.success = po.getSuccess();
        this.gmtCreate = po.getGmtCreate();
    }

    /**
     * @description 生成po
     * @return cn.edu.xmu.log.model.po.LogPo
     * @author Xianwei Wang
     * created at 11/18/20 2:59 PM
     */
    public LogPo createPo() {
        LogPo logPo = new LogPo();

        logPo.setUserId(this.userId);
        logPo.setIp(this.ip);
        logPo.setDescr(this.descr);
        logPo.setGmtCreate(this.gmtCreate);
        logPo.setPrivilegeId(this.privilegeId);
        logPo.setSuccess(this.success);

        return logPo;
    }

}
