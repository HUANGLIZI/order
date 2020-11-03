package cn.edu.xmu.privilege.mapper;

import cn.edu.xmu.privilege.model.po.PrivilegePo;

import java.util.List;

/**
 * 自定义的Mapper接口
 * @author Ming Qiu
 * @date Created in 2020/11/3 11:48
 **/
public interface PrivilegeMapper{
    /**
     * 返回所有权限数据
     * @return
     */
    List<PrivilegePo> selectAll();

}
