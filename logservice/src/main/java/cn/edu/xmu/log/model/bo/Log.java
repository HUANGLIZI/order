package cn.edu.xmu.log.model.bo;

import cn.edu.xmu.log.model.po.LogPo;
import cn.edu.xmu.log.model.vo.LogRetVo;
import cn.edu.xmu.log.model.vo.LogVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日志Bo
 * @Author 王纬策
 *
 */
@Data
public class Log implements VoObject {
    /**
     *
     * id 主键
     * user_id 用户ID
     * ip 登录的ip
     * desc 描述
     * privilege_id 权限ID
     */
    private Long id;
    private Long userId;
    private String ip;
    private String desc;
    private Long privilegeId;
    private LocalDateTime gmtCreate;
    private Byte success;

    private LocalDateTime beginDate;
    private LocalDateTime endDate;
    /**
     * 构造函数
     * @param po Po对象
     */
    public Log(LogPo po){
        this.setId(po.getId());
        this.setUserId(po.getUserId());
        this.setIp(po.getIp());
        this.setDesc(po.getDescr());
        this.setPrivilegeId(po.getPrivilegeId());
        this.setGmtCreate(po.getGmtCreate());
        this.setSuccess(po.getSuccess());
    }

    public Log(LogVo vo) {
        this.setUserId(vo.getUserId());
        this.setIp(vo.getIp());
        this.setPrivilegeId(vo.getPrivilegeId());
        this.setSuccess(vo.getSuccess());

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(vo.getBeginDate() != null)
        {
            this.setBeginDate(LocalDateTime.parse(vo.getBeginDate(), df));
        }
        else
        {
            this.setBeginDate(null);
        }
        if(vo.getEndDate() != null)
        {
            this.setEndDate(LocalDateTime.parse(vo.getEndDate(), df));
        }
        else
        {
            this.setEndDate(null);
        }
    }

    /**
     * Create return Vo object
     * @author 王纬策
     * @return
     */
    @Override
    public LogRetVo createVo() {
        LogRetVo logRetVo = new LogRetVo(this);
        return logRetVo;
    }

    /**
     * 创建SimpleVo
     * @return userSimpleRetVo
     * @author 王纬策
     */
    @Override
    public LogRetVo createSimpleVo() {
        LogRetVo logRetVo = new LogRetVo(this);
        return logRetVo;
    }

}
