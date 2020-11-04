package cn.edu.xmu.privilege.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.dao.RoleDao;
import cn.edu.xmu.privilege.model.bo.Role;
import cn.edu.xmu.privilege.model.vo.RoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoleService {
    @Autowired
    RoleDao roleDao;

    /**
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ReturnObject<List> selectAllRoles(Integer pageNum, Integer pageSize) {
        ReturnObject<List> returnObject = roleDao.selectAllRole(pageNum, pageSize);
        return returnObject;
    }

    /**
     * @param userId
     * @param vo
     * @return
     */
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
     * @param id
     * @return
     */
    public ReturnObject<Object> deleteRole(Long id) {
        return roleDao.deleteRole(id);
    }

    /**
     * @param userId
     * @param id
     * @param vo
     * @return
     */
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
