package cn.edu.xmu.privilege.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.dao.PrivilegeDao;
import cn.edu.xmu.privilege.dao.UserDao;
import cn.edu.xmu.privilege.model.bo.Privilege;
import cn.edu.xmu.privilege.model.bo.UserRole;
import cn.edu.xmu.privilege.model.vo.PrivilegeVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务
 * @author Ming Qiu
 **/
@Service
public class UserService {
    private Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private PrivilegeDao privilegeDao;

    @Autowired
    private UserDao userDao;


    /**
     * 赋予用户角色
     * @param createid 创建者id
     * @param userid 用户id
     * @param roleid 角色id
     * @return UserRole
     * @author Xianwei Wang
     * */
    public ReturnObject<VoObject> assignRole(Long createid, Long userid, Long roleid){
        return userDao.assignRole(createid, userid, roleid);
    }

    /**
     * 查看用户的角色信息
     * @param id 用户id
     * @return 角色信息
     * @author Xianwei Wang
     * */
    public ReturnObject<List> getSelfUserRoles(Long id){
        return userDao.getUserRoles(id);
    }


    /**
     * 查询所有权限
     * @return 权限列表
     */
    public ReturnObject<List> findAllPrivs(){
        ReturnObject<List>  ret = new ReturnObject<>(privilegeDao.findAllPrivs());
        return ret;
    }

    /**
     * 修改权限
     * @param id: 权限id
     * @return
     */
    public ReturnObject changePriv(Long id, PrivilegeVo vo){
        return privilegeDao.changePriv(id, vo);
    }
}
