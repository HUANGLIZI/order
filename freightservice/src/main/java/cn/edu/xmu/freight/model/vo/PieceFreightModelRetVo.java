package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.PieceFreightModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 这个类是 “通过运费模板Id获得件数运费模板明细 “ API的返回对象。
 * @author 李明明
 * @date 2020/12/12
 */

@Data
@ApiModel
public class PieceFreightModelRetVo
{
    @ApiModelProperty(value = "重量运费模板明细Id")
    private Long id;

    @ApiModelProperty(value = "抵达地区码")
    private Long regionId;

    @ApiModelProperty(value = "首件数")
    private Integer firstItems;

    @ApiModelProperty(value = "规则首件运费")
    private Long firstItemsPrice;

    @ApiModelProperty(value = "规则续件数")
    private Integer additionalItems;

    @ApiModelProperty(value = "规则续件运费")
    private Long additionalItemsPrice;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreated;

    /**
     * 用PieceFreightModel对象创建PieceFreightModelRetVo对象
     */
    public PieceFreightModelRetVo(PieceFreightModel bo)
    {
        this.id = bo.getId();
        this.regionId = bo.getRegionId();
        this.firstItems = bo.getFirstItem();
        this.firstItemsPrice = bo.getFirstItemsPrice();
        this.additionalItems = bo.getAdditionalItem();
        this.additionalItemsPrice = bo.getAdditionalItemsPrice();
        this.gmtModified = bo.getGmtModified();
        this.gmtCreated = bo.getGmtCreate();
    }
}
