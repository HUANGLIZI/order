package cn.edu.xmu.privilege.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.dao.RoleDao;
import cn.edu.xmu.privilege.model.bo.Role;
import cn.edu.xmu.privilege.model.vo.RoleVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 角色服务类
 *
 * @author 24320182203281 王纬策
 * createdBy 王纬策 2020/11/04 13:57
 * modifiedBy 王纬策 2020/11/7 19:20
 **/
@Service
public class RoleService {
    @Autowired
    RoleDao roleDao;

    /**
     * 分页查询所有角色
     *
     * @author 24320182203281 王纬策
     * @param pageNum  页数
     * @param pageSize 每页大小
     * @return ReturnObject<PageInfo < VoObject>> 分页返回角色信息
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    public ReturnObject<PageInfo<VoObject>> selectAllRoles(Integer pageNum, Integer pageSize) {
        ReturnObject<PageInfo<VoObject>> returnObject = roleDao.selectAllRole(pageNum, pageSize);
        return returnObject;
    }

    /**
     * 新增角色
     * @author 24320182203281 王纬策
     * @param userId 用户id
     * @param vo 角色视图
     * @return ReturnObject<VoObject> 角色返回视图
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    @Transactional
    public ReturnObject<VoObject> insertRole(Long userId, RoleVo vo) {
        Role role = vo.createRole();
        role.setCreatorId(userId);
        role.setGmtCreate(LocalDateTime.now());
        ReturnObject<Role> retObj = roleDao.insertRole(role);
        ReturnObject<VoObject> retRole = null;
        if (retObj.getCode().equals(ResponseCode.OK)) {
            retRole = new ReturnObject<>(retObj.getData());
        } else {
            retRole = new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
        }
        return retRole;
    }

    /**
     * 删除角色
     * @author 24320182203281 王纬策
     * @param id 角色id
     * @return ReturnObject<Object> 返回视图
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    @Transactional
    public ReturnObject<Object> deleteRole(Long id) {
        return roleDao.deleteRole(id);
    }

    /**
     * 修改角色
     * @author 24320182203281 王纬策
     * @param userId 用户id
     * @param id 角色id
     * @param vo 角色视图
     * @return ReturnObject<Object> 角色返回视图
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    @Transactional
    public ReturnObject<Object> updateRole(Long userId, Long id, RoleVo vo) {
        Role role = vo.createRole();
        role.setId(id);
        role.setCreatorId(userId);
        role.setGmtModified(LocalDateTime.now());
        ReturnObject<Role> retObj = roleDao.updateRole(role);
        ReturnObject<Object> retRole;
        if (retObj.getCode().equals(ResponseCode.OK)) {
            retRole = new ReturnObject<>(retObj.getData());
        } else {
            retRole = new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
        }
        return retRole;
    }
}
