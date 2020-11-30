package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.PieceFreightModelChangeBo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Caixin
 * @date 2020-11-28 23:20
 */

@Data
@ApiModel(description = "管理员修改店铺的运费模板")
public class PieceFreightModelChangeVo {

    @ApiModelProperty(value = "收件数")
    private Integer firstItems;

    @ApiModelProperty(value = "规则首件运费")
    private Long firstItemsPrice;

    @ApiModelProperty(value = "规则续件数")
    private Integer additionalItems;

    @ApiModelProperty(value = "规则续件运费")
    private Long additionalItemsPrice;

    @ApiModelProperty(value = "抵达地区码")
    private Long regionId;

    public PieceFreightModelChangeBo createPieceFreightModelChangeBo()
    {
        PieceFreightModelChangeBo pieceFreightModelChangeBo = new PieceFreightModelChangeBo();
        pieceFreightModelChangeBo.setAdditionalItems(this.additionalItems);
        pieceFreightModelChangeBo.setAdditionalItemsPrice(this.additionalItemsPrice);
        pieceFreightModelChangeBo.setFirstItems(this.firstItems);
        pieceFreightModelChangeBo.setFirstItemsPrice(this.firstItemsPrice);
        pieceFreightModelChangeBo.setRegionId(this.regionId);

        return pieceFreightModelChangeBo;
    }
}
