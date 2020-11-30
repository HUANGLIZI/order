package cn.edu.xmu.freight.model.bo;

import cn.edu.xmu.freight.model.po.FreightModelPo;
import cn.edu.xmu.freight.model.vo.FreightModelRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FreightModel implements VoObject, Serializable {
    private Long id;
    private Long shopId;
    private String name;
    private String defaultModel;
    private Byte type;
    private Integer unit;
    private LocalDateTime gmtCreated;
    private LocalDateTime gmtModified;

    public FreightModel() {
    }

    /**
     * 构造函数
     */
    public FreightModel(FreightModelPo po) {
        this.id = po.getId();
        this.name = po.getName();
        this.shopId = po.getShopId();
        this.defaultModel = po.getDefaultModel();
        this.type = po.getType();
        this.unit=po.getUnit();
        this.gmtCreated = po.getGmtCreated();
        this.gmtModified = po.getGmtModified();
    }

    /**
     * 生成FreightModelRetVo对象作为返回前端
     */
    @Override
    public Object createVo() {
        return new FreightModelRetVo(this);
    }

    /**
     * 生成FreightModelSimpleRetVo对象作为返回前端
     */
    @Override
    public Object createSimpleVo() {
        return new FreightModelRetVo(this);
    }

//    /**
//     * 用vo对象创建更新po对象
//     */
//    public FreightModelPo createUpdatePo(FreightModelVo vo){
//        FreightModelPo po = new FreightModelPo();
//        po.setId(this.getId());
//        po.setName(vo.getName());
//        po.setShopId(null);
//        po.setDefaultModel(null);
//        po.setType(null);
//        po.setUnit(null);
//        po.setGmtCreated(null);
//        po.setGmtModified(LocalDateTime.now());
//        return po;
//    }
//
//    /**
//     * 用bo对象创建更新po对象
//     */
//    public FreightModelPo getFreightModelPo() {
//        FreightModelPo po = new FreightModelPo();
//        po.setId(this.getId());
//        po.setName(vo.getName());
//        po.setShopId(null);
//        po.setDefaultModel(null);
//        po.setType(null);
//        po.setUnit(null);
//        po.setGmtCreated(null);
//        po.setGmtModified(LocalDateTime.now());
//        return po;
//    }
}
