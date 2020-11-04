package cn.edu.xmu.privilege.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.dao.RoleDao;
import cn.edu.xmu.privilege.model.bo.Role;
import cn.edu.xmu.privilege.model.po.RolePo;
import cn.edu.xmu.privilege.model.vo.RoleRetVo;
import cn.edu.xmu.privilege.model.vo.RoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {
    @Autowired
    RoleDao roleDao;

    public ReturnObject<List<RoleRetVo>> selectAllRoles(Integer pageNum, Integer pageSize) {
        ReturnObject<List<Role>> returnObject = roleDao.selectAllRole(pageNum,pageSize);
        ReturnObject<List<RoleRetVo>> retObject = null;
        List<RoleRetVo> roleRetVos = new ArrayList<>(returnObject.getData().size());
        if (returnObject.getCode().equals(ResponseCode.OK)) {
            for(Role role : returnObject.getData()){
                roleRetVos.add(role.createVo());
            }
            retObject = new ReturnObject<>(roleRetVos);
        }else{
            retObject = new ReturnObject<>(returnObject.getCode(), returnObject.getErrmsg());
        }
        return retObject;
    }

    public ReturnObject<VoObject> insertRole(RoleVo vo){
        Role role = vo.createRole();
        role.setCreatorId(1L);
        role.setGmtCreate(LocalDateTime.now());
        ReturnObject<Role> retObj = roleDao.insertRole(role);
        ReturnObject<VoObject> retRole = null;
        if (retObj.getCode().equals(ResponseCode.OK)) {
            retRole = new ReturnObject<>(retObj.getData());
        }else{
            retRole = new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
        }
        return retRole;
    }

    public ReturnObject<Object> deleteRole(Long id){
        return roleDao.deleteRole(id);
    }

    public ReturnObject<Object> updateRole(Long id, RoleVo vo){
        Role role = vo.createRole();
        role.setId(id);
        role.setCreatorId(1L);
        role.setGmtModified(LocalDateTime.now());
        ReturnObject<Role> retObj = roleDao.updateRole(role);
        ReturnObject<Object> retRole;
        if (retObj.getCode().equals(ResponseCode.OK)) {
            retRole = new ReturnObject<>(retObj.getData());
        }else{
            retRole = new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
        }
        return retRole;
    }
}
