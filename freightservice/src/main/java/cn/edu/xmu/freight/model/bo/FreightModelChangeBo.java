package cn.edu.xmu.freight.model.bo;

import cn.edu.xmu.freight.model.po.FreightModelPo;
import cn.edu.xmu.freight.model.vo.FreightModelChangeVo;

/**
 * @author Caixin
 * @date 2020-11-26 11:19
 */
public class FreightModelChangeBo {

    /**
     * 代理对象
     */
    private FreightModelPo freightModelPo;

    /**
     * 构造函数
     */
    public FreightModelChangeBo() {
        this.freightModelPo = new FreightModelPo();
    }

    public FreightModelChangeBo(FreightModelPo freightModelPo) {

        this.freightModelPo = freightModelPo;
    }

    public FreightModelPo gotFreightModelPo()
    {
        return this.freightModelPo;
    }

    public Integer getUnit() {

        return freightModelPo.getUnit();
    }

    public void setUnit(Integer unit) {

        freightModelPo.setUnit(unit);
    }

    public String getName() {

        return freightModelPo.getName();
    }

    public void setName(String name) {

        freightModelPo.setName(name);
    }

    public Long getShopId() {

        return freightModelPo.getShopId();
    }

    public void setShopId(Long shopId) {

        freightModelPo.setShopId(shopId);
    }

    public Long getId() {

        return freightModelPo.getId();
    }

    public void setId(Long id) {

        freightModelPo.setId(id);
    }
}
