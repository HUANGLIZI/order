package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.FreightModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "运费FreightModelRetVo模板返回视图对象")
public class FreightModelRetVo {

    @ApiModelProperty(value = "运费模板id")
    private Long id;

    @ApiModelProperty(value = "运费模板名称")
    private String name;

    @ApiModelProperty(value = "是否为默认运费模板")
    private String defaultModel;

    @ApiModelProperty(value = "运费模板类型")
    private Byte type;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreated;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    /**
     * 用bo对象生成vo对象
     */
    public FreightModelRetVo(FreightModel freightModel){
        this.id=freightModel.getId();
        this.name=freightModel.getName();
        this.defaultModel= freightModel.getDefaultModel();
        this.type=freightModel.getType();
        this.gmtCreated=freightModel.getGmtCreated();
        this.gmtModified=freightModel.getGmtModified();
    }

}
