package cn.edu.xmu.freight.model.bo;

import cn.edu.xmu.freight.model.po.PieceFreightModelPo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PieceFreightModel {
    private Long id;
    private Long freightModelId;
    private Integer firstItems;
    private Long firstItemsPrice;
    private Integer additionalItems;
    private Long additionalItemsPrice;
    private Long regionId;
    private LocalDateTime gmtCreated;
    private LocalDateTime gmtModified;

    /**
     * 构造函数
     */
    public PieceFreightModel(PieceFreightModelPo po){
        this.id=po.getId();
        this.freightModelId=po.getFreightModelId();
        this.firstItems=po.getFirstItems();
        this.firstItemsPrice=po.getFirstItemsPrice();
        this.additionalItems=po.getAdditionalItems();
        this.additionalItemsPrice=po.getAdditionalItemsPrice();
        this.regionId=po.getRegionId();
        this.gmtCreated=po.getGmtCreated();
        this.gmtModified=po.getGmtModified();
    }
}
