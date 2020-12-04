package cn.edu.xmu.freight.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "重量模板视图对象")
public class WeightFreightModelVo {

    @ApiModelProperty(value = "首重")
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
    @ApiModelProperty(value = "总价格")
    private Long abovePrice;
    @ApiModelProperty(value = "抵达地区码")
    private Long regionId;


}
