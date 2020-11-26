package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.ordermodule.model.bo.FreightModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "运费模板信息视图对象")
public class FreightModelVo {

    private Long shopId;

    @ApiModelProperty(value = "模板名字")
    private String name;

    @ApiModelProperty(value = "缺省模板")
    private String defaultModel;

    @ApiModelProperty(value = "模板类型")
    private Byte type;

    private Integer unit;

    @ApiModelProperty(name = "创建时间", value = "gmtCreate")
    private LocalDateTime gmtCreated;

    @ApiModelProperty(name = "修改时间", value = "gmtModified")
    private LocalDateTime gmtModified;


    public FreightModel createFreightModel() {
        FreightModel freightModel = new FreightModel();
        freightModel.setName(this.name);
        //freightModel.setId(this.name);
        return freightModel;
    }

}
