package cn.edu.xmu.freight.model.bo;

import cn.edu.xmu.freight.model.po.WeightFreightModelPo;
import cn.edu.xmu.freight.model.vo.WeightFreightModelRetVo;
import cn.edu.xmu.freight.model.vo.WeightFreightModelVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class WeightFreightModel implements VoObject, Serializable {

    private Long id;

    private Long freightModelId;

    private Long firstWeight;

    private Long firstWeightFreight;

    private Long tenPrice;

    private Long fiftyPrice;

    private Long hundredPrice;

    private Long trihunPrice;

    private Long abovePrice;

    private Long regionId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public WeightFreightModel(WeightFreightModelVo vo){
        this.firstWeight=vo.getFirstWeight();
        this.firstWeightFreight=vo.getFirstWeightFreight();
        this.tenPrice=vo.getTenPrice();
        this.firstWeightFreight=vo.getFirstWeightFreight();
        this.tenPrice=vo.getTenPrice();
        this.hundredPrice=vo.getHundredPrice();
        this.trihunPrice=vo.getTrihunPrice();
        this.abovePrice=vo.getAbovePrice();
        this.regionId=vo.getRegionId();

    }

     public WeightFreightModel(WeightFreightModelPo po){
        this.id=po.getId();
        this.freightModelId=po.getFreightModelId();
        this.firstWeight=po.getFirstWeight();
        this.firstWeightFreight=po.getFirstWeightFreight();
        this.tenPrice=po.getTenPrice();
        this.firstWeightFreight=po.getFirstWeightFreight();
        this.tenPrice=po.getTenPrice();
        this.fiftyPrice=po.getFiftyPrice();
        this.hundredPrice=po.getHundredPrice();
        this.trihunPrice=po.getTrihunPrice();
        this.abovePrice=po.getAbovePrice();
        this.regionId=po.getRegionId();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }
    
    public WeightFreightModelPo gotWeightFreightModelPo() {
        WeightFreightModelPo po = new WeightFreightModelPo();
        po.setId(this.getId());
        po.setRegionId(this.getRegionId());
        po.setAbovePrice(this.getAbovePrice());
        po.setFiftyPrice(this.getFiftyPrice());
        po.setFirstWeight(this.getFirstWeight());
        po.setFirstWeightFreight(this.getFirstWeightFreight());
        po.setFreightModelId(this.getFreightModelId());
        po.setHundredPrice(this.getHundredPrice());
        po.setTenPrice(this.getTenPrice());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        po.setTrihunPrice(this.getTrihunPrice());
        return po;
    }

    @Override
    public Object createVo() {
        return new WeightFreightModelRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
