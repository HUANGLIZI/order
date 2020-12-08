package cn.edu.xmu.oomall.other.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.other.model.AftersaleDTO;

/**
 * @author Caixin
 * @date 2020-12-05 21:45
 */
public interface IAftersaleService {

    /**
     * 通过aftersaleId查找userId
     * @param aftersaleId
     * @return Long
     * @author Cai Xinlu
     * @date 2020-12-05 21:45
     */
    ReturnObject<AftersaleDTO> findUserIdbyAftersaleId(Long aftersaleId);

    ReturnObject<AftersaleDTO> findShopIdbyAftersaleId(Long aftersaleId);
}
