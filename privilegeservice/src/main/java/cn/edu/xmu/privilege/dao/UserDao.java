package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.*;
import cn.edu.xmu.privilege.mapper.RolePoMapper;
import cn.edu.xmu.ooad.util.AES;
import cn.edu.xmu.ooad.util.SHA256;
import cn.edu.xmu.ooad.util.StringUtil;
import cn.edu.xmu.ooad.util.*;
import cn.edu.xmu.privilege.mapper.UserPoMapper;
import cn.edu.xmu.privilege.mapper.UserProxyPoMapper;
import cn.edu.xmu.privilege.mapper.UserRolePoMapper;
import cn.edu.xmu.privilege.model.bo.Role;
import cn.edu.xmu.privilege.model.bo.User;
import cn.edu.xmu.privilege.model.bo.UserRole;
import cn.edu.xmu.privilege.model.po.*;
import cn.edu.xmu.privilege.model.vo.UserEditVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Ming Qiu
 * @date Created in 2020/11/1 11:48
 * Modified in 2020/11/3 14:37
 **/
@Repository
public class UserDao implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    /**
     * 是否初始化，生成signature和加密
     */
    @Value("${prvilegeservice.initialization}")
    private Boolean initialization;

    @Value("${prvilegeservice.user.expiretime}")
    private long timeout;

    @Value("${prvilegeservice.randomtime}")
    private int randomTime;

    @Autowired
    private UserRolePoMapper userRolePoMapper;

    @Autowired
    private UserProxyPoMapper userProxyPoMapper;

    @Autowired
    private UserPoMapper userMapper;

    @Autowired
    private RolePoMapper rolePoMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RoleDao roleDao;

    /**
     * 取消用户角色
     * @param id 用户角色id
     * @return ReturnObject<VoObject>
     * @author Xianwei Wang
     * */
    public ReturnObject<VoObject> revokeRole(Long id){
        UserRolePo userRolePo = userRolePoMapper.selectByPrimaryKey(id);
        if (userRolePo == null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        clearUserPrivCache(userRolePo.getUserId());

        int state = userRolePoMapper.deleteByPrimaryKey(id);
        if (state == 0){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject<>();
    }

    /**
     * 赋予用户角色
     * @param createid 创建者id
     * @param userid 用户id
     * @param roleid 角色id
     * @return ReturnObject<VoObject>
     * @author Xianwei Wang
     * */
    public ReturnObject<VoObject> assignRole(Long createid, Long userid, Long roleid){
        UserRolePo userRolePo = new UserRolePo();
        userRolePo.setUserId(userid);
        userRolePo.setRoleId(roleid);

        User user = getUserById(userid);
        User create = getUserById(createid);
        RolePo rolePo = rolePoMapper.selectByPrimaryKey(roleid);

        //用户id或角色id不存在
        if (user == null || create == null || rolePo == null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        userRolePo.setCreatorId(createid);
        userRolePo.setGmtCreate(LocalDateTime.now());

        UserRole userRole = new UserRole(userRolePo, user, new Role(rolePo), create);
        userRolePo.setSignature(userRole.getCacuSignature());

        //查询该用户是否已经拥有该角色
        UserRolePoExample example = new UserRolePoExample();
        UserRolePoExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userid);
        criteria.andRoleIdEqualTo(roleid);

        //若未拥有，则插入数据
        if (userRolePoMapper.selectByExample(example).isEmpty()){
            clearUserPrivCache(userid);
            userRolePoMapper.insert(userRolePo);
        }

        return new ReturnObject<>(userRole);

    }

    /**
     * 使用用户id，清空该用户和被代理对象的redis缓存
     * @param userid 用户id
     * @author Xianwei Wang
     */
    private void clearUserPrivCache(Long userid){
        String key = "u_" + userid;
        redisTemplate.delete(key);

        UserProxyPoExample example = new UserProxyPoExample();
        UserProxyPoExample.Criteria criteria = example.createCriteria();
        criteria.andUserBIdEqualTo(userid);
        List<UserProxyPo> userProxyPoList = userProxyPoMapper.selectByExample(example);

        for (UserProxyPo user:
             userProxyPoList) {
            String proxyKey = "up_" + user.getUserAId();
            redisTemplate.delete(proxyKey);
        }
    }

    /**
     * 获取用户的角色信息
     * @param id 用户id
     * @return UserRole列表
     * @author Xianwei Wang
     * */
    public ReturnObject<List> getUserRoles(Long id){
        UserRolePoExample example = new UserRolePoExample();
        UserRolePoExample.Criteria criteria = example.createCriteria();

        criteria.andUserIdEqualTo(id);

        List<UserRolePo> userRolePoList = userRolePoMapper.selectByExample(example);
        logger.debug("getUserRoles: userId = "+ id + "roleNum = "+ userRolePoList.size());

        List<UserRole> retUserRoleList = new ArrayList<>(userRolePoList.size());

        for (UserRolePo po : userRolePoList) {
            User user = getUserById(po.getUserId());
            User creator = getUserById(po.getCreatorId());
            Role role = new Role(rolePoMapper.selectByPrimaryKey(po.getRoleId()));

            UserRole userRole = new UserRole(po, user, role, creator);

            //校验签名
            if (userRole.authetic()){
                retUserRoleList.add(userRole);
                logger.debug("getRoleIdByUserId: userId = " + po.getUserId() + " roleId = " + po.getRoleId());
            } else {
                logger.error("getUserRoles: Wrong Signature(auth_user_role): id =" + po.getId());
            }
        }
        return new ReturnObject<>(retUserRoleList);
    }

    /***
     * 根据id查找用户
     * @param id 用户id
     * @return 用户,若签名校验失败返回null
     * @author Xianwei Wang
     */
    private User getUserById(Long id){
        UserPo userPo = userMapper.selectByPrimaryKey(id);
        if (userPo == null){
            return null;
        }

        User user = new User(userPo);
        if (! user.authetic()){
            logger.error("getUser: Wrong Signature(auth_user): id =" + id);
            return null;
        }
        return user;
    }



    /**
     * 计算User自己的权限，load到Redis
     *
     * @param id userID
     * @return void
     * createdBy: Ming Qiu 2020-11-02 11:44
     * modifiedBy: Ming Qiu 2020-11-03 12:31
     * 将获取用户Roleid的代码独立, 增加redis过期时间
     */
    private void loadSingleUserPriv(Long id) {
        List<Long> roleIds = this.getRoleIdByUserId(id);
        String key = "u_" + id;
        if (roleIds.size() == 0){
            redisTemplate.opsForSet().add(key, "0");
        } else {
            Set<String> roleKeys = new HashSet<>(roleIds.size());
            for (Long roleId : roleIds) {
                String roleKey = "r_" + roleId;
                roleKeys.add(roleKey);
                if (!redisTemplate.hasKey(roleKey)) {
                    roleDao.loadRolePriv(roleId);
                }
            }
            redisTemplate.opsForSet().unionAndStore(roleKeys, key);
        }
        redisTemplate.expire(key, this.timeout + new Random().nextInt(randomTime), TimeUnit.SECONDS);
    }

    /**
     * 获得用户的角色id
     *
     * @param id 用户id
     * @return 角色id列表
     * createdBy: Ming Qiu 2020/11/3 13:55
     */
    private List<Long> getRoleIdByUserId(Long id) {
        UserRolePoExample example = new UserRolePoExample();
        UserRolePoExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(id);
        List<UserRolePo> userRolePoList = userRolePoMapper.selectByExample(example);
        logger.debug("getRoleIdByUserId: userId = "+ id + "roleNum = "+ userRolePoList.size());
        List<Long> retIds = new ArrayList<>(userRolePoList.size());
        for (UserRolePo po : userRolePoList) {
            StringBuilder signature = StringUtil.concatString("-",
                    po.getUserId().toString(), po.getRoleId().toString(), po.getCreatorId().toString());
            String newSignature = SHA256.getSHA256(signature.toString());


            if (newSignature.equals(po.getSignature())) {
                retIds.add(po.getRoleId());
                logger.debug("getRoleIdByUserId: userId = " + po.getUserId() + " roleId = " + po.getRoleId());
            } else {
                logger.error("getRoleIdByUserId: Wrong Signature(auth_role_privilege): id =" + po.getId());
            }
        }
        return retIds;
    }

    /**
     * 计算User的权限（包括代理用户的权限，只计算直接代理用户），load到Redis
     *
     * @param id userID
     * @return void
     * createdBy Ming Qiu 2020/11/1 11:48
     * modifiedBy Ming Qiu 2020/11/3 14:37
     */
    public void loadUserPriv(Long id) {

        String key = "u_" + id;
        String aKey = "up_" + id;

        List<Long> proxyIds = this.getProxyIdsByUserId(id);
        List<String> proxyUserKey = new ArrayList<>(proxyIds.size());
        for (Long proxyId : proxyIds) {
            if (!redisTemplate.hasKey("u_" + proxyId)) {
                logger.debug("loadUserPriv: loading proxy user. proxId = "+ proxyId);
                loadSingleUserPriv(proxyId);
            }
            proxyUserKey.add("u_" + proxyId);
        }
        if (!redisTemplate.hasKey(key)) {
            logger.debug("loadUserPriv: loading user. id = "+ id);
            loadSingleUserPriv(id);
        }
        redisTemplate.opsForSet().unionAndStore(key, proxyUserKey, aKey);
        redisTemplate.expire(aKey, this.timeout + new Random().nextInt(randomTime), TimeUnit.SECONDS);
    }

    /**
     * 获得代理的用户id列表
     *
     * @param id 用户id
     * @return 被代理的用户id
     * createdBy Ming Qiu 14:37
     */
    private List<Long> getProxyIdsByUserId(Long id) {
        UserProxyPoExample example = new UserProxyPoExample();
        //查询当前所有有效的被代理用户
        UserProxyPoExample.Criteria criteria = example.createCriteria();
        criteria.andUserAIdEqualTo(id);
        criteria.andValidEqualTo((byte) 1);
        List<UserProxyPo> userProxyPos = userProxyPoMapper.selectByExample(example);
        List<Long> retIds = new ArrayList<>(userProxyPos.size());
        LocalDateTime now = LocalDateTime.now();
        for (UserProxyPo po : userProxyPos) {
            StringBuilder signature = StringUtil.concatString("-", po.getUserAId().toString(),
                    po.getUserBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), po.getValid().toString());
            String newSignature = SHA256.getSHA256(signature.toString());
            UserProxyPo newPo = null;

            if (newSignature.equals(po.getSignature())) {
                if (now.isBefore(po.getEndDate()) && now.isAfter(po.getBeginDate())) {
                    //在有效期内
                    retIds.add(po.getUserBId());
                    logger.debug("getProxyIdsByUserId: userAId = " + po.getUserAId() + " userBId = " + po.getUserBId());
                } else {
                    //代理过期了，但标志位依然是有效
                    newPo = newPo == null ? new UserProxyPo() : newPo;
                    newPo.setValid((byte) 0);
                    signature = StringUtil.concatString("-", po.getUserAId().toString(),
                            po.getUserBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), newPo.getValid().toString());
                    newSignature = SHA256.getSHA256(signature.toString());
                    newPo.setSignature(newSignature);
                }
            } else {
                logger.error("getProxyIdsByUserId: Wrong Signature(auth_user_proxy): id =" + po.getId());
            }

            if (null != newPo) {
                logger.debug("getProxyIdsByUserId: writing back.. po =" + newPo);
                userProxyPoMapper.updateByPrimaryKeySelective(newPo);
            }
        }
        return retIds;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (! initialization){
            return;
        }
        //初始化user
        UserPoExample example = new UserPoExample();
        UserPoExample.Criteria criteria = example.createCriteria();
        criteria.andSignatureIsNull();

        List<UserPo> userPos = userMapper.selectByExample(example);

        for (UserPo po : userPos){
            UserPo newPo = new UserPo();
            newPo.setPassword(AES.encrypt(po.getPassword(), User.AESPASS));
            newPo.setEmail(AES.encrypt(po.getEmail(), User.AESPASS));
            newPo.setMobile(AES.encrypt(po.getMobile(), User.AESPASS));
            newPo.setName(AES.encrypt(po.getName(), User.AESPASS));
            newPo.setId(po.getId());

            StringBuilder signature = StringUtil.concatString("-", po.getUserName(), newPo.getPassword(),
                    newPo.getMobile(),newPo.getEmail(),po.getOpenId(),po.getState().toString(),po.getDepartId().toString(),
                    po.getCreatorId().toString());
            newPo.setSignature(SHA256.getSHA256(signature.toString()));

            userMapper.updateByPrimaryKeySelective(newPo);
        }

        //初始化UserProxy
        UserProxyPoExample example1 = new UserProxyPoExample();
        UserProxyPoExample.Criteria criteria1 = example1.createCriteria();
        criteria1.andSignatureIsNull();
        List<UserProxyPo> userProxyPos = userProxyPoMapper.selectByExample(example1);

        for (UserProxyPo po : userProxyPos) {
            UserProxyPo newPo = new UserProxyPo();
            newPo.setId(po.getId());
            StringBuilder signature = StringUtil.concatString("-", po.getUserAId().toString(),
                    po.getUserBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), po.getValid().toString());
            String newSignature = SHA256.getSHA256(signature.toString());
            newPo.setSignature(newSignature);
            userProxyPoMapper.updateByPrimaryKeySelective(newPo);
        }

        //初始化UserRole
        UserRolePoExample example3 = new UserRolePoExample();
        UserRolePoExample.Criteria criteria3 = example3.createCriteria();
        criteria3.andSignatureIsNull();
        List<UserRolePo> userRolePoList = userRolePoMapper.selectByExample(example3);
        for (UserRolePo po : userRolePoList) {
            StringBuilder signature = StringUtil.concatString("-",
                    po.getUserId().toString(), po.getRoleId().toString(), po.getCreatorId().toString());
            String newSignature = SHA256.getSHA256(signature.toString());

            UserRolePo newPo = new UserRolePo();
            newPo.setId(po.getId());
            newPo.setSignature(newSignature);
            userRolePoMapper.updateByPrimaryKeySelective(newPo);
        }

    }

    /* auth009 */

    /**
     * 根据 id 修改用户信息
     * @param userEditVo 传入的 User 对象
     * @return 返回对象 ReturnObj
     */
    public ReturnObject<Object> modifyUserByVo(Long id, UserEditVo userEditVo) {
        // 查询密码等资料以计算新签名
        UserPo orig = userMapper.selectByPrimaryKey(id);
        // 不修改已被逻辑废弃的账户
        if (orig == null || (orig.getState() != null && User.State.getTypeByCode(orig.getState().intValue()) == User.State.DELETE)) {
            logger.info("用户不存在或已被删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        // 构造 User 对象以计算签名
        User user = new User(orig);
        UserPo po = user.createUpdatePo(userEditVo);

        // 将更改的联系方式 (如发生变化) 的已验证字段改为 false
        if (userEditVo.getEmail() != null && !userEditVo.getEmail().equals(user.getEmail())) {
            po.setEmailVerified((byte) 0);
        }
        if (userEditVo.getMobile() != null && !userEditVo.getMobile().equals(user.getMobile())) {
            po.setMobileVerified((byte) 0);
        }

        // 更新数据库
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = userMapper.updateByPrimaryKeySelective(po);
        } catch (DataAccessException e) {
            // 如果发生 Exception，判断是邮箱还是啥重复错误
            if (Objects.requireNonNull(e.getMessage()).contains("auth_user.auth_user_mobile_uindex")) {
                logger.info("电话重复：" + userEditVo.getMobile());
                retObj = new ReturnObject<>(ResponseCode.MOBILE_REGISTERED);
            } else if (e.getMessage().contains("auth_user.auth_user_email_uindex")) {
                logger.info("邮箱重复：" + userEditVo.getEmail());
                retObj = new ReturnObject<>(ResponseCode.EMAIL_REGISTERED);
            } else {
                // 其他情况属未知错误
                logger.error("数据库错误：" + e.getMessage());
                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                        String.format("发生了严重的数据库错误：%s", e.getMessage()));
            }
            return retObj;
        } catch (Exception e) {
            // 其他 Exception 即属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        // 检查更新有否成功
        if (ret == 0) {
            logger.info("用户不存在或已被删除：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("用户 id = " + id + " 的资料已更新");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    /**
     * (物理) 删除用户
     * @param id 用户 id
     * @return 返回对象 ReturnObj
     */
    public ReturnObject<Object> physicallyDeleteUser(Long id) {
        ReturnObject<Object> retObj;
        int ret = userMapper.deleteByPrimaryKey(id);
        if (ret == 0) {
            logger.info("用户不存在或已被删除：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("用户 id = " + id + " 已被永久删除");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    /**
     * 创建可改变目标用户状态的 Po
     * @param id 用户 id
     * @param state 用户目标状态
     * @return UserPo 对象
     */
    private UserPo createUserStateModPo(Long id, User.State state) {
        // 查询密码等资料以计算新签名
        UserPo orig = userMapper.selectByPrimaryKey(id);
        // 不修改已被逻辑废弃的账户的状态
        if (orig == null || (orig.getState() != null && User.State.getTypeByCode(orig.getState().intValue()) == User.State.DELETE)) {
            return null;
        }

        // 构造 User 对象以计算签名
        User user = new User(orig);
        user.setState(state);
        // 构造一个全为 null 的 vo 因为其他字段都不用更新
        UserEditVo vo = new UserEditVo();

        return user.createUpdatePo(vo);
    }

    /**
     * 改变用户状态
     * @param id 用户 id
     * @param state 目标状态
     * @return 返回对象 ReturnObj
     */
    public ReturnObject<Object> changeUserState(Long id, User.State state) {
        UserPo po = createUserStateModPo(id, state);
        if (po == null) {
            logger.info("用户不存在或已被删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = userMapper.updateByPrimaryKeySelective(po);
            if (ret == 0) {
                logger.info("用户不存在或已被删除：id = " + id);
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                logger.info("用户 id = " + id + " 的状态修改为 " + state.getDescription());
                retObj = new ReturnObject<>();
            }
        } catch (DataAccessException e) {
            // 数据库错误
            logger.error("数据库错误：" + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 属未知错误
            logger.error("严重错误：" + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        return retObj;
    }

    /* auth009 ends */
}

