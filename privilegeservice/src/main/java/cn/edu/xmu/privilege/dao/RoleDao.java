package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.encript.SHA256;
import cn.edu.xmu.privilege.mapper.*;
import cn.edu.xmu.privilege.model.bo.Privilege;
import cn.edu.xmu.privilege.model.bo.Role;
import cn.edu.xmu.privilege.model.po.*;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 角色访问类
 *
 * @author Weice Wang
 * @date Created in 2020/11/4 11:48
 * Modified in 2020/11/5 14:16
 **/
@Repository
public class RoleDao implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(RoleDao.class);

    /**
     * 是否初始化，生成signature和加密
     */
    @Value("${privilegeservice.initialization}")
    private Boolean initialization;

    @Value("${privilegeservice.role.expiretime}")
    private long timeout;

    @Autowired
    private RolePoMapper roleMapper;

    @Autowired
    private UserRolePoMapper userRolePoMapper;

    @Autowired
    private UserProxyPoMapper userProxyPoMapper;

    @Autowired
    private RolePrivilegePoMapper rolePrivilegePoMapper;

    @Autowired
    private PrivilegeDao privDao;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 根据角色Id,查询角色的所有权限
     * @author yue hao
     * @param id 角色ID
     * @return 角色的权限列表
     */
    public List<Privilege> findPrivsByRoleId(Long id) {
        //getPrivIdsByRoleId已经进行role的签名校验
        List<Long> privIds = this.getPrivIdsByRoleId(id);
        List<Privilege> privileges = new ArrayList<>();
        for(Long privId: privIds) {
            Privilege po = this.privDao.findPriv(privId);
            logger.debug("getPriv:  po = " + po);
            privileges.add(po);
        }
        return privileges;
    }

    /**
     * 将一个角色的所有权限id载入到Redis
     *
     * @param id 角色id
     * @return void
     *
     * createdBy: Ming Qiu 2020-11-02 11:44
     * ModifiedBy: Ming Qiu 2020-11-03 12:24
     * 将读取权限id的代码独立为getPrivIdsByRoleId. 增加redis值的有效期
     *            Ming Qiu 2020-11-07 8:00
     * 集合里强制加“0”
     */
    public void loadRolePriv(Long id) {
        List<Long> privIds = this.getPrivIdsByRoleId(id);
        String key = "r_" + id;
        for (Long pId : privIds) {
            redisTemplate.opsForSet().add(key, pId);
        }
        redisTemplate.opsForSet().add(key,0);
        long randTimeout = Common.addRandomTime(this.timeout);
        redisTemplate.expire(key, randTimeout, TimeUnit.SECONDS);
    }

    /**
     * 由Role Id 获得 Privilege Id 列表
     *
     * @param id: Role id
     * @return Privilege Id 列表
     * created by Ming Qiu in 2020/11/3 11:48
     */
    private List<Long> getPrivIdsByRoleId(Long id) {
        RolePrivilegePoExample example = new RolePrivilegePoExample();
        RolePrivilegePoExample.Criteria criteria = example.createCriteria();
        criteria.andRoleIdEqualTo(id);
        List<RolePrivilegePo> rolePrivilegePos = rolePrivilegePoMapper.selectByExample(example);
        List<Long> retIds = new ArrayList<>(rolePrivilegePos.size());
        for (RolePrivilegePo po : rolePrivilegePos) {
            StringBuilder signature = Common.concatString("-", po.getRoleId().toString(),
                    po.getPrivilegeId().toString(), po.getCreatorId().toString());
            String newSignature = SHA256.getSHA256(signature.toString());

            if (newSignature.equals(po.getSignature())) {
                retIds.add(po.getPrivilegeId());
                logger.debug("getPrivIdsBByRoleId: roleId = " + po.getRoleId() + " privId = " + po.getPrivilegeId());
            } else {
                logger.info("getPrivIdsBByRoleId: Wrong Signature(auth_role_privilege): id =" + po.getId());
            }
        }
        return retIds;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (!initialization){
            return;
        }

        RolePrivilegePoExample example = new RolePrivilegePoExample();
        RolePrivilegePoExample.Criteria criteria = example.createCriteria();
        criteria.andSignatureIsNull();
        List<RolePrivilegePo> rolePrivilegePos = rolePrivilegePoMapper.selectByExample(example);
        List<Long> retIds = new ArrayList<>(rolePrivilegePos.size());
        for (RolePrivilegePo po : rolePrivilegePos) {
            StringBuilder signature = Common.concatString("-", po.getRoleId().toString(),
                    po.getPrivilegeId().toString(), po.getCreatorId().toString());
            String newSignature = SHA256.getSHA256(signature.toString());
            RolePrivilegePo newPo = new RolePrivilegePo();
            newPo.setId(po.getId());
            newPo.setSignature(newSignature);
            rolePrivilegePoMapper.updateByPrimaryKeySelective(newPo);
        }

    }

    /**
     * 分页查询所有角色
     * @param pageNum
     * @param pageSize
     * @return ReturnObject<List>
     */
    public ReturnObject<List> selectAllRole(Integer pageNum, Integer pageSize) {
        RolePoExample example = new RolePoExample();
        RolePoExample.Criteria criteria = example.createCriteria();
        //分页查询
        PageHelper.startPage(pageNum, pageSize);
        logger.debug("page = " + pageNum + "pageSize = " + pageSize);
        //不加限定条件查询所有
        List<RolePo> rolePos = roleMapper.selectByExample(example);
        List<Role> roles = new ArrayList<>(rolePos.size());
        logger.debug("selectAllRoles: total roles num = " + rolePos.size());
        for (RolePo rolePoItem : rolePos) {
            Role item = new Role(rolePoItem);
            roles.add(item);
        }
        return new ReturnObject<>(roles);
    }

    /**
     * 增加一个角色
     * @param role
     * @return ReturnObject<Role>
     */
    public ReturnObject<Role> insertRole(Role role) {
        RolePo rolePo = role.gotRolePo();
        ReturnObject<Role> retObj = null;
        try{
            int ret = roleMapper.insertSelective(rolePo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertRole: insert role fail " + rolePo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + rolePo.getName()));
            } else {
                //插入成功
                logger.debug("insertRole: insert role = " + rolePo.toString());
                role.setId(rolePo.getId());
                retObj = new ReturnObject<>(role);
            }
        }
        catch (DataAccessException e) {
            if (Objects.requireNonNull(e.getMessage()).contains("auth_role.auth_role_name_uindex")) {
                //若有重复的角色名则新增失败
                logger.debug("updateRole: have same role name = " + rolePo.getName());
                retObj = new ReturnObject<>(ResponseCode.ROLE_REGISTERED, String.format("角色名重复：" + rolePo.getName()));
            } else {
                // 其他数据库错误
                logger.debug("other sql exception : " + e.getMessage());
                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    /**
     * 删除一个角色
     * @param id
     * @return ReturnObject<Object>
     */
    public ReturnObject<Object> deleteRole(Long id) {
        ReturnObject<Object> retObj = null;
        int ret = roleMapper.deleteByPrimaryKey(id);
        if (ret == 0) {
            //删除角色表
            logger.debug("deleteRole: id not exist = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("角色id不存在：" + id));
        } else {
            //删除角色权限表
            logger.debug("deleteRole: delete role id = " + id);
            RolePrivilegePoExample exampleRP = new RolePrivilegePoExample();
            RolePrivilegePoExample.Criteria criteriaRP = exampleRP.createCriteria();
            criteriaRP.andRoleIdEqualTo(id);
            List<RolePrivilegePo> rolePrivilegePos = rolePrivilegePoMapper.selectByExample(exampleRP);
            logger.debug("deleteRole: delete role-privilege num = " + rolePrivilegePos.size());
            for (RolePrivilegePo rolePrivilegePo : rolePrivilegePos) {
                rolePrivilegePoMapper.deleteByPrimaryKey(rolePrivilegePo.getId());
            }
            //删除缓存中角色权限信息
            redisTemplate.delete("r_" + id);
            //删除用户角色表
            UserRolePoExample exampleUR = new UserRolePoExample();
            UserRolePoExample.Criteria criteriaUR = exampleUR.createCriteria();
            criteriaUR.andRoleIdEqualTo(id);
            List<UserRolePo> userRolePos = userRolePoMapper.selectByExample(exampleUR);
            logger.debug("deleteRole: delete user-role num = " + userRolePos.size());
            for (UserRolePo userRolePo : userRolePos) {
                userRolePoMapper.deleteByPrimaryKey(userRolePo.getId());
                //删除缓存中具有删除角色的用户权限
                redisTemplate.delete("u_" + userRolePo.getUserId());
                redisTemplate.delete("up_" + userRolePo.getUserId());
                //查询当前所有有效的代理具有删除角色用户的代理用户
                UserProxyPoExample example = new UserProxyPoExample();
                UserProxyPoExample.Criteria criteria = example.createCriteria();
                criteria.andUserBIdEqualTo(userRolePo.getUserId());
                List<UserProxyPo> userProxyPos = userProxyPoMapper.selectByExample(example);
                for(UserProxyPo userProxyPo : userProxyPos){
                    //删除缓存中代理了具有删除角色的用户的代理用户
                    redisTemplate.delete("u_" + userProxyPo.getUserAId());
                    redisTemplate.delete("up_" + userProxyPo.getUserAId());
                }
            }
            retObj = new ReturnObject<>();
        }

        return retObj;
    }

    /**
     * 修改一个角色
     * @param role
     * @return ReturnObject<Role>
     */
    public ReturnObject<Role> updateRole(Role role) {
        RolePo rolePo = role.gotRolePo();
        ReturnObject<Role> retObj = null;
        try{
            int ret = roleMapper.updateByPrimaryKeySelective(rolePo);
            if (ret == 0) {
                //修改失败
                logger.debug("updateRole: update role fail : " + rolePo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("角色id不存在：" + rolePo.getId()));
            } else {
                //修改成功
                logger.debug("updateRole: update role = " + rolePo.toString());
                retObj = new ReturnObject<>();
            }
        }
        catch (DataAccessException e) {
            if (Objects.requireNonNull(e.getMessage()).contains("auth_role.auth_role_name_uindex")) {
                //若有重复的角色名则修改失败
                logger.debug("updateRole: have same role name = " + rolePo.getName());
                retObj = new ReturnObject<>(ResponseCode.ROLE_REGISTERED, String.format("角色名重复：" + rolePo.getName()));
            } else {
                // 其他数据库错误
                logger.debug("other sql exception : " + e.getMessage());
                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }
}
