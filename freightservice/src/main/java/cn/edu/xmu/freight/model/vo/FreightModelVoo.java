package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.po.FreightModelPo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;


@ApiModel(description = "运费模板信息视图对象1")
@Data
public class FreightModelVoo {
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
    public FreightModelPo createFreightModel() {
        FreightModelPo  freightModelPo = new FreightModelPo();
        freightModelPo.setType(this.type);
        freightModelPo.setName(this.name);
        freightModelPo.setUnit(this.unit);
        return freightModelPo;
    }
}
