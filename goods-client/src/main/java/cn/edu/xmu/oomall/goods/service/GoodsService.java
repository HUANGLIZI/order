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

    /**
     * 通过skuId查找shopId
     */
    ReturnObject<List<Long>> getShopIdBySkuId(List<Long> skuIds);
}
