package cn.edu.xmu.log.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 权限传值对象
 *
 * @author Di Han Li
 * @date Created in 2020/11/4 9:08
 * Modified by 24320182203221 李狄翰 at 2020/11/8 8:00
 **/
@Data
@ApiModel("日志传值对象")
public class LogVo {

    @ApiModelProperty(name = "开始时间", value = "beginTime")
    private String beginTime;

    @ApiModelProperty(name = "结束时间", value = "endTime")
    private String endTime;


}