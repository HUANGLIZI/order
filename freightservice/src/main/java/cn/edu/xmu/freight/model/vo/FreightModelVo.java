package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.FreightModel;
import cn.edu.xmu.freight.model.po.FreightModelPo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;


@ApiModel(description = "运费模板信息视图对象1")
@Data
public class FreightModelVo {
    @ApiModelProperty(name = "模板名")
    private String name;

    @ApiModelProperty(name = "类型")
    private Byte type;

    @ApiModelProperty(name = "计重单位")
    private Integer unit;

    /**
     * 构造函数
     * @author 24320182203227 李子晗
     * @return freightModelPo
     */
    public FreightModel createFreightModel() {
        FreightModel freightModel = new FreightModel();
        freightModel.setType(this.type);
        freightModel.setName(this.name);
        freightModel.setUnit(this.unit);
        return freightModel;
    }
}
