package cn.edu.xmu.oomall.other.service;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.other.model.AftersaleDTO;
import cn.edu.xmu.oomall.other.model.CustomerDTO;

import java.util.List;

/**
 * @author Caixin
 * @date 2020-12-05 21:45
 */
public interface IAftersaleService {
    /**
     * 通过aftersaleId查找userId
     */
    ReturnObject<Long> findUserIdbyAftersaleId(Long aftersaleId);

    /**
     * 通过aftersaleId查找shopId
     */
    ReturnObject<Long> findShopIdbyAftersaleId(Long aftersaleId);

    /**
     * 通过userId查找用户信息
     * @param userId
     * @return
     */
    ReturnObject<CustomerDTO> findCustomerByUserId(Long userId);

    /**
     * @author 洪晓杰
     * 通过aftersaleId查找orderItemId
     */
    ReturnObject<Long> findOrderItemIdbyAftersaleId(Long aftersaleId);


    /**
     * 通过skuId清购物车
     */
    ReturnObject<ResponseCode> deleteGoodsInCart(Long customerId, List<Long> skuIdList);
}