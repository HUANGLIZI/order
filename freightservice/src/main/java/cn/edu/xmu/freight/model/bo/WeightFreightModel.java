package cn.edu.xmu.freight.model.bo;

import cn.edu.xmu.freight.model.po.WeightFreightModelPo;
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

    private LocalDateTime gmtCreated;

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
        po.setGmtCreated(this.getGmtCreated());
        po.setGmtModified(this.getGmtModified());
        po.setTrihunPrice(this.getTrihunPrice());
        return po;
    }


    ////????????????????????????????????????????????????????///////
    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
