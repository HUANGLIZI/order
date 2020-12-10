package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.FreightDTO;

/**
 * @author Caixin
 * @date 2020-12-09 17:11
 */
public interface IFreightService {

    /**
     * 计算运费模板
     * @param freightDTO
     * @return Integer
     * @author Cai Xinlu
     * @date 2020-12-09 17:12
     */
    ReturnObject<Integer> calculateFreightPrice(FreightDTO freightDTO);
}
