package cn.edu.xmu.freight.model.bo;

import cn.edu.xmu.freight.model.po.PieceFreightModelPo;

/**
 * @author Caixin
 * @date 2020-11-28 23:25
 */
public class PieceFreightModelChangeBo {

    /**
     * 代理对象
     */
    PieceFreightModelPo pieceFreightModelPo;

    /**
     * 构造函数
     */
    public PieceFreightModelChangeBo()
    {
        this.pieceFreightModelPo = new PieceFreightModelPo();
    }
    
    public PieceFreightModelChangeBo(PieceFreightModelPo pieceFreightModelPo)
    {
        this.pieceFreightModelPo = pieceFreightModelPo;
    }

    public PieceFreightModelPo gotPieceFreightModelPo()
    {
        return this.pieceFreightModelPo;
    }

    public Long getId() {

        return pieceFreightModelPo.getId();
    }

    public void setId(Long id) {

        pieceFreightModelPo.setId(id);
    }

    public Integer getFirstItems() {

        return pieceFreightModelPo.getFirstItems();
    }

    public void setFirstItems(Integer firstItems) {

        pieceFreightModelPo.setFirstItems(firstItems);
    }

    public Long getFirstItemsPrice() {

        return pieceFreightModelPo.getFirstItemsPrice();
    }

    public void setFirstItemsPrice(Long firstItemsPrice) {

        pieceFreightModelPo.setFirstItemsPrice(firstItemsPrice);
    }

    public Integer getAdditionalItems() {

        return pieceFreightModelPo.getAdditionalItems();
    }

    public void setAdditionalItems(Integer additionalItems) {

        pieceFreightModelPo.setAdditionalItems(additionalItems);
    }

    public Long getAdditionalItemsPrice() {

        return pieceFreightModelPo.getAdditionalItemsPrice();
    }

    public void setAdditionalItemsPrice(Long additionalItemsPrice) {

        pieceFreightModelPo.setAdditionalItemsPrice(additionalItemsPrice);
    }

    public Long getRegionId() {

        return pieceFreightModelPo.getRegionId();
    }

    public void setRegionId(Long regionId){

        pieceFreightModelPo.setRegionId(regionId);
    }
}
