package cn.edu.xmu.oomall.other.service;

/**
 * 地址服务调用接口
 *
 * @author wwc
 * @date 2020/12/1 10:20
 * @version 1.0
 */
public interface IAddressService {

    /**
     * 查询该地区id是否被废弃
     *
     * @author wwc
     * @date 2020/12/01 09:11
     * @version 1.0
     */
    Boolean getValidRegionId(Long regionId);

}
