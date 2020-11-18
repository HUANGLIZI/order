package cn.edu.xmu.log.model.vo;

import cn.edu.xmu.log.model.bo.Log;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 日志接受Vo
 * @Author 王纬策
 *
 */
@Data
@ApiModel(description = "日志传值对象")
public class LogVo {
    private Long userId;
    private String ip;
    private Long privilegeId;
    private Byte success;
    private String beginDate;
    private String endDate;

    public Log createBo() {
        Log operationLog = new Log(this);
        return operationLog;
    }
}
