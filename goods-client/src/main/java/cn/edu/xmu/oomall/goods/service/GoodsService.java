package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.ooad.util.ResponseCode;
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
     * 根据shopId查找店铺信息
     * @param shopId
     * @return ShopDetailDTO
     * @author Li Zihan
     * @date 2020-12-09 17:03
     */
    ReturnObject<ShopDetailDTO> getShopInfoByShopId(Long shopId);

    /**
     * 根据skuId查找商品信息
     * 0普通活动或者可能是秒杀  1团购  2预售  3优惠券
     * quantity可正可负
     * @param skuId
     * @return GoodsDetailDTO
     * @author Cai Xinlu
     * @date 2020-12-09 17:03
     */
    ReturnObject<GoodsDetailDTO> getGoodsBySkuId(Long skuId, Byte type, Long activityId, Integer quantity);

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
     * @author 24320182203227 李子晗
     * @param skuId
     * @return GoodsFreightDTO
     */
    ReturnObject<GoodsFreightDTO> getGoodsFreightDetailBySkuId(Long skuId);

    /**
     * @author 24320182203327 张湘君
     *将所有运费模板值为freightId的spu改为默认运费模板
     */
    ReturnObject<Boolean> updateSpuFreightId(Long freightModelId);

    /**
     * @author Cai Xinlu
     * 通过skuId查找shopId
     */
    ReturnObject<List<Long>> getShopIdBySkuId(List<Long> skuIds);

    /**
     * @author Cai Xinlu
     * 通知商品模块扣库存
     */
    ReturnObject<ResponseCode> signalDecrInventory(List<Long> skuIds, List<Integer> quantity);

    /**
     * 根据activityId获得优惠活动Json字符串
     * @author Li Zihan
     * @param activityId
     * @return RetrunObject<String>
     */
    ReturnObject<List<String>> getActivityRule(Long couponId,List<Long> activityId);
}
