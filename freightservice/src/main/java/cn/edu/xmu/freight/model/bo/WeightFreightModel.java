package cn.edu.xmu.freight.model.bo;

import cn.edu.xmu.freight.model.po.WeightFreightModelPo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WeightFreightModel{
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


    /**
     * 构造函数
     */
    public WeightFreightModel(WeightFreightModelPo po) {
        this.id = po.getId();
        this.freightModelId = po.getFreightModelId();
        this.firstWeight = po.getFirstWeight();
        this.firstWeightFreight = po.getFirstWeightFreight();
        this.tenPrice = po.getTenPrice();
        this.fiftyPrice=po.getFiftyPrice();
        this.hundredPrice=po.getHundredPrice();
        this.trihunPrice=po.getTrihunPrice();
        this.abovePrice=po.getAbovePrice();
        this.regionId=po.getRegionId();
        this.gmtCreated = po.getGmtCreated();
        this.gmtModified = po.getGmtModified();
    }
}
