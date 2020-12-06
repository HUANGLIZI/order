package cn.edu.xmu.order.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;

@Data
@ApiModel(description = "sku列表视图对象")
public class SkuItemsVo {

    @NotBlank(message = "列表不能为空")
    @ApiModelProperty(value = "sku列表")
    private ArrayList<SkuItem> skuItems;
}

@Data
@ApiModel(description = "skuItem视图对象")
class SkuItem {
    @NotBlank(message = "skuId不能为空")
    @ApiModelProperty(value = "skuId")
    private int skuId;

    @NotBlank(message = "数量不能为空")
    @ApiModelProperty(value = "购买数量")
    private int count;
}
