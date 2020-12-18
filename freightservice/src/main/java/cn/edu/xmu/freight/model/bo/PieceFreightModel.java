package cn.edu.xmu.freight.model.bo;


import cn.edu.xmu.freight.model.po.PieceFreightModelPo;
import cn.edu.xmu.freight.model.vo.PieceFreightModelRetVo;
import cn.edu.xmu.freight.model.vo.PieceFreightModelVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PieceFreightModel implements VoObject, Serializable {

    private Long id;

    private Long freightModelId;

    private Integer firstItem;

    private Long firstItemsPrice;

    private Integer additionalItem;

    private Long additionalItemsPrice;

    private Long regionId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public PieceFreightModel(PieceFreightModelVo vo) {
        this.firstItem = vo.getFirstItem();
        this.firstItemsPrice = vo.getFirstItemPrice();
        this.additionalItem = vo.getAdditionalItems();
        this.additionalItemsPrice = vo.getAdditionalItemsPrice();
        this.regionId = vo.getRegionId();
    }

    public PieceFreightModel(PieceFreightModelPo po) {
        this.id=po.getId();
        this.freightModelId=po.getFreightModelId();
        this.firstItem = po.getFirstItems();
        this.firstItemsPrice = po.getFirstItemsPrice();
        this.additionalItem = po.getAdditionalItems();
        this.additionalItemsPrice = po.getAdditionalItemsPrice();
        this.regionId = po.getRegionId();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }
    
    @Override
    public Object createVo() {
        return new PieceFreightModelRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

    public PieceFreightModelPo gotPieceFreightModelPo() {
        PieceFreightModelPo po = new PieceFreightModelPo();
        po.setId(this.getId());
        po.setRegionId(this.getRegionId());
        po.setAdditionalItems(this.getAdditionalItem());
        po.setAdditionalItemsPrice(this.getAdditionalItemsPrice());
        po.setFirstItems(this.getFirstItem());
        po.setFirstItemsPrice(this.getFirstItemsPrice());
        po.setFreightModelId(this.getFreightModelId());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        return po;

    }
}
