package cn.edu.xmu.freight.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "件数模板视图对象")
public class PieceFreightModelVo {

    @ApiModelProperty(value = "抵达地区码")
    private Long regionId;
    @ApiModelProperty(value = "首件数")
    private Integer firstItem;
    @ApiModelProperty(value = "规则首件运费")
    private Long firstItemPrice;
    @ApiModelProperty(value = "规则续件数")
    private Integer additionalItems;
    @ApiModelProperty(value = "规则续件运费")
    private Long additionalItemsPrice;


}
