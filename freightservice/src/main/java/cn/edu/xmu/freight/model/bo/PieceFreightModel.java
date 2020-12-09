package cn.edu.xmu.freight.model.bo;


import cn.edu.xmu.freight.model.po.PieceFreightModelPo;
import cn.edu.xmu.freight.model.vo.PieceFreightModelVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PieceFreightModel implements VoObject, Serializable {

    private Long id;

    private Long freightModelId;

    private Integer firstItems;

    private Long firstItemsPrice;

    private Integer additionalItems;

    private Long additionalItemsPrice;

    private Long regionId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public PieceFreightModel(PieceFreightModelVo vo) {
        this.firstItems = vo.getFirstItems();
        this.firstItemsPrice = vo.getFirstItemsPrice();
        this.additionalItems = vo.getAdditionalItems();
        this.additionalItemsPrice = vo.getAdditionalItemsPrice();
        this.regionId = vo.getRegionId();
    }

    public PieceFreightModel(PieceFreightModelPo po) {
        this.id=po.getId();
        this.freightModelId=po.getFreightModelId();
        this.firstItems = po.getFirstItems();
        this.firstItemsPrice = po.getFirstItemsPrice();
        this.additionalItems = po.getAdditionalItems();
        this.additionalItemsPrice = po.getAdditionalItemsPrice();
        this.regionId = po.getRegionId();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }
    
    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

    public PieceFreightModelPo gotPieceFreightModelPo() {
        PieceFreightModelPo po = new PieceFreightModelPo();
        po.setId(this.getId());
        po.setRegionId(this.getRegionId());
        po.setAdditionalItems(this.getAdditionalItems());
        po.setAdditionalItemsPrice(this.getAdditionalItemsPrice());
        po.setFirstItems(this.getFirstItems());
        po.setFirstItemsPrice(this.getFirstItemsPrice());
        po.setFreightModelId(this.getFreightModelId());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        return po;

    }
}
