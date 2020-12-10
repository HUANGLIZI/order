package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.GoodsDetailDTO;
import cn.edu.xmu.oomall.goods.model.GoodsFreightDTO;
import cn.edu.xmu.oomall.goods.model.ShopDetailDTO;

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

    /**
     * 根据skuId查找店铺信息
     * @param skuId
     * @return ShopDetailDTO
     * @author Cai Xinlu
     * @date 2020-12-09 17:03
     */
    ReturnObject<ShopDetailDTO> getShopInfoBySkuId(Long skuId);

    /**
     * 根据skuId查找商品信息
     * @param skuId
     * @return GoodsDetailDTO
     * @author Cai Xinlu
     * @date 2020-12-09 17:03
     */
    ReturnObject<GoodsDetailDTO> getGoodsBySkuId(Long skuId);

    /**
     * 判断三个Id是否有效
     * @param couponId presaleId grouponId
     * @return Boolean
     * @author Cai Xinlu
     * @date 2020-12-09 17:04
     */
    ReturnObject<Boolean> judgeActivityIdValid(Long couponId, Long presaleId, Long grouponId);

    /**
     * 判断Id是否有效
     * @param couponActivityId
     * @return Boolean
     * @author Cai Xinlu
     * @date 2020-12-09 17:04
     */
    ReturnObject<Boolean> judgeCouponActivityIdValid(Long couponActivityId);

    /**
     * 获得默认运费模板
     * @param skuId
     * @return GoodsFreightDTO
     */
    ReturnObject<GoodsFreightDTO> getGoodsFreightDetailBySkuId(Long skuId);

    /**
     *将所有运费模板值为freightId的spu改为默认运费模板
     */
    ReturnObject<Boolean> updateSpuFreightId(Long freightModelId);
}
