package cn.edu.xmu.log.model.bo;

import cn.edu.xmu.log.model.po.LogPo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Log implements Serializable {
    private Long id;
    private Long userId;
    private String ip;
    private String descr;
    private LocalDateTime gmtCreate;
    private Long privilegeId;
    private Byte success;

    public Log(LogPo logPo) {
        this.id = logPo.getId();
        this.userId = logPo.getUserId();
        this.ip = logPo.getIp();
        this.descr = logPo.getDescr();
        this.gmtCreate = logPo.getGmtCreate();
        this.privilegeId = logPo.getPrivilegeId();
        this.success = logPo.getSuccess();
    }
    public Log(){}

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
