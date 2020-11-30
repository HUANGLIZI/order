package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.WeightFreightModelChangeBo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Caixin
 * @date 2020-11-26 10:23
 */

@Data
@ApiModel(description = "店家或管理员修改重量运费模板明细")
public class WeightFreightModelChangeVo {

    @ApiModelProperty(value = "首次重量")
    private Long firstWeight;

    @ApiModelProperty(value = "首重价格")
    private Long firstWeightFreight;

    @ApiModelProperty(value = "10kg以下每0.5kg价格")
    private Long tenPrice;

    @ApiModelProperty(value = "50kg以下每0.5kg价格")
    private Long fiftyPrice;

    @ApiModelProperty(value = "100kg以下每0.5kg价格")
    private Long hundredPrice;

    @ApiModelProperty(value = "300kg以下每0.5kg价格")
    private Long trihunPrice;

    @ApiModelProperty(value = "300kg以上每0.5kg价格")
    private Long abovePrice;

    @ApiModelProperty(value = "抵达地区码")
    private Long regionId;

    public WeightFreightModelChangeBo createWeightFreightModelBo()
    {
        WeightFreightModelChangeBo weightFreightModelChangeBo = new WeightFreightModelChangeBo();
        weightFreightModelChangeBo.setFirstWeight(this.firstWeight);
        weightFreightModelChangeBo.setFirstWeightFreight(this.firstWeightFreight);
        weightFreightModelChangeBo.setTenPrice(this.tenPrice);
        weightFreightModelChangeBo.setFiftyPrice(this.fiftyPrice);
        weightFreightModelChangeBo.setHundredPrice(this.hundredPrice);
        weightFreightModelChangeBo.setTrihunPrice(this.trihunPrice);
        weightFreightModelChangeBo.setAbovePrice(this.abovePrice);
        weightFreightModelChangeBo.setRegionId(this.regionId);

        return weightFreightModelChangeBo;
    }
}
