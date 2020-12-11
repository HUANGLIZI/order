package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.FreightModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FreightModelRetVo {
    @ApiModelProperty(value = "模板id")
    private Long id;

    @ApiModelProperty(value = "部门id")
    private Long shopId;

    @ApiModelProperty(value = "名字")
    private String name;

    @ApiModelProperty(value = "默认模板")
    private Byte defaultModel;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreated;

    @ApiModelProperty(value = "类型")
    private Byte type;

    @ApiModelProperty(value = "计费方式")
    private Integer unit;



    /**
     * 用FreightModel对象建立Vo对象
     *
     * @author 24320182203227 李子晗
     */
    public FreightModelRetVo(FreightModel freightModel) {
        this.id = freightModel.getId();
        this.name = freightModel.getName();
        //this.shopId = freightModel.getShopId();
        this.defaultModel = freightModel.getDefaultModel();
        this.type = freightModel.getType();
        this.gmtCreated = freightModel.getGmtCreate();
        this.gmtModified = freightModel.getGmtModified();
        this.unit=freightModel.getUnit();
    }
}
