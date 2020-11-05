package cn.edu.xmu.privilege.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.dao.PrivilegeDao;
import cn.edu.xmu.privilege.dao.UserDao;
import cn.edu.xmu.privilege.model.bo.Privilege;
import cn.edu.xmu.privilege.model.vo.PrivilegeVo;
import cn.edu.xmu.privilege.model.vo.UserVo;
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

    /**
     * 根据 ID 和 UserVo 修改任意用户信息
     * @param id 用户 id
     * @param vo UserVo 对象
     * @return
     */
    public ReturnObject<Object> modifyUserInfo(Long id, UserVo vo) {
        return userDao.modifyUserByVo(id, vo);
    }

    /**
     * 根据 id 删除任意用户
     * @param id 用户 id
     * @return
     */
    public ReturnObject<Object> deleteUser(Long id) {
        // 注：逻辑删除
        return userDao.logicallyDeleteUser(id);
    }

    /**
     * 根据 id 禁止任意用户登录
     * @param id 用户 id
     * @return
     */
    public ReturnObject<Object> forbidUser(Long id) {
        return userDao.forbidUser(id);
    }

    /**
     * 解禁任意用户
     * @param id 用户 id
     * @return
     */
    public ReturnObject<Object> releaseUser(Long id) {
        return userDao.releaseUser(id);
    }
}
