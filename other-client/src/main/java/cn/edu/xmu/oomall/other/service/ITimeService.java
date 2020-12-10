package cn.edu.xmu.oomall.other.service;


import cn.edu.xmu.ooad.util.ReturnObject;

import java.util.List;

/**
 * 时段服务调用接口
 * @author wwc
 * @date 2020/11/26 10:20
 * @version 1.0
 */
public interface ITimeService {

    /**
     * @author cxr
     * 获取当前类型为type的所有时间段的id
     * @param type (秒杀/广告)
     * @return 时间段列表
     */
    List<Long> listSelectAllTimeSegmentId(Byte type);

    /**
     * @author cxr
     * 根据id查询（秒杀/广告）时间段是否存在
     * @param type 时段类型
     * @param id 时间段id
     * @return ReturnObject
     */
    ReturnObject getTimeSegmentId(Byte type, Long id);

    /**
     * @author cxr
     * 获得当前（秒杀/广告）时间段id
     * @return Long 时间段id
     */
    Long getCurrentSegmentId(Byte type);
}
