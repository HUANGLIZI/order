package cn.edu.xmu.freight.model.bo;

import cn.edu.xmu.freight.model.po.FreightModelPo;
import cn.edu.xmu.freight.model.vo.FreightModelRetVo;
import cn.edu.xmu.freight.model.vo.FreightModelVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FreightModel implements VoObject, Serializable {
    private Long id;

    private Long shopId;

    private String name;

    private Byte defaultModel;

    private Byte type;

    private Integer unit;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public FreightModel() {
    }

    /**
     * 构造函数
     *
     * @param po 用PO构造
     * @return FreightModel
     */
    public FreightModel(FreightModelPo po) {
        this.id = po.getId();
        this.shopId = po.getShopId();
        this.name = po.getName();
        this.defaultModel = po.getDefaultModel();
        this.type = po.getType();
        this.unit = po.getUnit();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }


//    /**
//     * 用vo对象创建更新po对象
//     *
//     * @param vo vo对象
//     * @return po对象
//     */
//    public FreightModelPo createUpdatePo(FreightModelVo vo){
//        FreightModelPo po = new FreightModelPo();
//        po.setId(null);
//        po.setShopId(vo.getShopId());
//        po.setName(vo.getName());
//        po.setDefaultModel(vo.getDefaultModel());
//        po.setGmtCreated(vo.getGmtCreated());
//        po.setGmtModified(LocalDateTime.now());
//
//        return po;
//    }


    @Override
    public Object createVo() {
        return new FreightModelRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }


    public FreightModelPo gotFreightModelPo() {
        FreightModelPo po = new FreightModelPo();
        po.setId(this.getId());
        po.setShopId(this.getShopId());
        po.setName(this.getName());
        po.setUnit(this.getUnit());
        po.setType(this.getType());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        po.setDefaultModel(this.getDefaultModel());
        return po;
    }

}
