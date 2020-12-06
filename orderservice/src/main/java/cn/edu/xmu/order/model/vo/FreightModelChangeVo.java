package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.payment.model.bo.FreightModelChangeBo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Caixin
 * @date 2020-11-26 10:23
 */

@Data
@ApiModel(description = "店家或管理员修改数量运费模板明细")
public class FreightModelChangeVo {

    @ApiModelProperty(value = "运费模板名")
    private String name;

    @ApiModelProperty(value = "计量单位500g")
    private Integer unit;


    public FreightModelChangeBo createFreightModelBo()
    {
        FreightModelChangeBo freightModelChangeBo = new FreightModelChangeBo();
        freightModelChangeBo.setUnit(this.unit);
        freightModelChangeBo.setName(this.name);

        return freightModelChangeBo;
    }


}
