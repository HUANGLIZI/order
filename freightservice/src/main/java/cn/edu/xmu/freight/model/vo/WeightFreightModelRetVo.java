package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.WeightFreightModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 这个类是 “通过运费模板Id获得重量运费模板明细 “ API的返回对象。
 * @author 李明明
 * @date 2020/12/12
 */
@Data
@ApiModel
public class WeightFreightModelRetVo
{
    @ApiModelProperty(value = "件数运费模板明细Id")
    private Long id;

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

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreated;

    /**
     * 用WeightFreightModel对象创建WeightFreightModelRetVo对象
     * @author 李明明
     */
    public WeightFreightModelRetVo(WeightFreightModel bo)
    {
        this.id = bo.getId();
        this.firstWeight = bo.getFirstWeight();
        this.firstWeightFreight = bo.getFirstWeightFreight();
        this.tenPrice = bo.getTenPrice();
        this.fiftyPrice = bo.getFiftyPrice();
        this.hundredPrice = bo.getHundredPrice();
        this.trihunPrice = bo.getTrihunPrice();
        this.abovePrice = bo.getAbovePrice();
        this.regionId = bo.getRegionId();
        this.gmtCreated = bo.getGmtCreate();
        this.gmtModified = bo.getGmtModified();
    }
}
