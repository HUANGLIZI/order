package cn.edu.xmu.oomall.other.service;


import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;

import java.util.List;

public interface IFootprintService {

    /**
     * 增加足迹
     */
    ReturnObject<ResponseCode> postFootprint(Long customerId, Long skuId);
}
