package cn.edu.xmu.privilege.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.dao.RoleDao;
import cn.edu.xmu.privilege.model.bo.Role;
import cn.edu.xmu.privilege.model.vo.RoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色服务类
 *
 * @author Weice Wang
 * @date Created in 2020/11/4 11:48
 * Modified in 2020/11/4 12:16
 **/
@Service
public class RoleService {
    @Autowired
    RoleDao roleDao;

    /**
     * 分页查询所有角色
     * @param pageNum
     * @param pageSize
     * @return ReturnObject<List>
     */
    public ReturnObject<List> selectAllRoles(Integer pageNum, Integer pageSize) {
        ReturnObject<List> returnObject = roleDao.selectAllRole(pageNum, pageSize);
        return returnObject;
    }

    /**
     * 新增角色
     * @param userId
     * @param vo
     * @return ReturnObject<VoObject>
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
     * @param id
     * @return ReturnObject<Object>
     */
    @Transactional
    public ReturnObject<Object> deleteRole(Long id) {
        return roleDao.deleteRole(id);
    }

    /**
     * 修改角色
     * @param userId
     * @param id
     * @param vo
     * @return ReturnObject<Object>
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
