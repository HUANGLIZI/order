package cn.edu.xmu.oomall.goods.service;

import java.util.List;

/**
 * @author Qiuyan Qian
 * @date Created in 2020/12/01 09:23
 */
public interface GoodsService {

    /**
     * 根据shopId获取当前shop所有spuId的list
     * @param shopId
     * java.util.List<java.lang.Long>
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/1 上午9:26
    */
    List<Long> getAllSpuIdByShopId(Long shopId);

    /**
     * 根据spuid查询shopid外部调用接口
     * @param spuId 商品SPUID
     * @return java.lang.Long
     * @author Fiber W.
     * created at 11/24/20 10:34 AM
     */
    Long getShopIdBySpuId(Long spuId);
}
