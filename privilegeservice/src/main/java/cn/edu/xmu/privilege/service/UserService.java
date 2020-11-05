package cn.edu.xmu.privilege.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.AES;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.dao.PrivilegeDao;
import cn.edu.xmu.privilege.dao.UserDao;
import cn.edu.xmu.privilege.model.bo.Privilege;
import cn.edu.xmu.privilege.model.bo.User;
import cn.edu.xmu.privilege.model.bo.User;
import cn.edu.xmu.privilege.model.vo.PrivilegeVo;
import cn.edu.xmu.privilege.model.vo.UserEditVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 用户服务
 * @author Ming Qiu
 * Modified at 2020/11/5 10:39
 **/
@Service
public class UserService {
    private Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private PrivilegeDao privilegeDao;

    @Autowired
    private UserDao userDao;

    @Value("${loginconfig.multiply}")
    private Boolean canMultiplyLogin;

    //    @Autowired
    private JwtHelper jwtHelper = new JwtHelper();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

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

    public ReturnObject<String> Login(String username, String password, String IpAddr)
    {
        ReturnObject<String> retObj = null;
        User user = userDao.getUserByName(username);

        if(user == null || !password.equals(AES.decrypt(user.getPassword(), User.AESPASS))){
            retObj = new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT, "用户名或密码错误");
            return retObj;
        }
        if (user.getState() != User.State.NORM){
            retObj = new ReturnObject<>(ResponseCode.AUTH_USER_FORBIDDEN, "您的状态为" + user.getState().getDescription());
            return retObj;
        }
        if (!user.authetic()){
            retObj = new ReturnObject<>(ResponseCode.AUTH_USER_FORBIDDEN, "您的信息被篡改，请联系管理员处理");
            return retObj;
        }
        if(redisTemplate.hasKey("up_" + user.getId().toString()) && canMultiplyLogin == false){
            // 用户重复登录处理
            Set<String> set = redisTemplate.opsForSet().members("up_" + user.getId().toString());
            /* 找出JWT */
            String jwt = null;
            for (String str : set) {
                if(str.contains(".")){
                    jwt = str;
                    break;
                }
            }

            /* 将JWT加入需要踢出的集合 */
            redisTemplate.delete("up_" + user.getId().toString());
            userDao.banJwt(jwt);
        }

        String jwt = jwtHelper.createToken(user.getId(),user.getDepartId());
        userDao.loadUserPriv(user.getId(), jwt);
        userDao.setLoginIPAndPosition(user.getId(),IpAddr, LocalDateTime.now());
        retObj = new ReturnObject<>(jwt);

        return retObj;
    }

    public ReturnObject<Boolean> Logout(Long userId)
    {
        ReturnObject<Boolean> retObj = null;
        Boolean success = redisTemplate.delete("up_" + userId);
        if (success){
            retObj = new ReturnObject<>(true);
        } else {
            retObj = new ReturnObject<>(ResponseCode.AUTH_ID_NOTEXIST,"您尚未登录，无需注销");
        }
        return retObj;
    }

    /**
     * 根据 ID 和 UserEditVo 修改任意用户信息
     * @param id 用户 id
     * @param vo UserEditVo 对象
     * @return 返回对象 ReturnObject
     */
    @Transactional
    public ReturnObject<Object> modifyUserInfo(Long id, UserEditVo vo) {
        return userDao.modifyUserByVo(id, vo);
    }

    /**
     * 根据 id 删除任意用户
     * @param id 用户 id
     * @return 返回对象 ReturnObject
     */
    @Transactional
    public ReturnObject<Object> deleteUser(Long id) {
        // 注：逻辑删除
        return userDao.changeUserState(id, User.State.DELETE);
    }

    /**
     * 根据 id 禁止任意用户登录
     * @param id 用户 id
     * @return 返回对象 ReturnObject
     */
    @Transactional
    public ReturnObject<Object> forbidUser(Long id) {
        return userDao.changeUserState(id, User.State.FORBID);
    }

    /**
     * 解禁任意用户
     * @param id 用户 id
     * @return 返回对象 ReturnObject
     */
    @Transactional
    public ReturnObject<Object> releaseUser(Long id) {
        return userDao.changeUserState(id, User.State.NORM);
    }

}
