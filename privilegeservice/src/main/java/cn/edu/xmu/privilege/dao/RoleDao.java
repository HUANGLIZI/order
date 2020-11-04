package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.SHA256;
import cn.edu.xmu.ooad.util.StringUtil;
import cn.edu.xmu.privilege.mapper.RolePoMapper;
import cn.edu.xmu.privilege.mapper.RolePrivilegePoMapper;
import cn.edu.xmu.privilege.mapper.UserRolePoMapper;
import cn.edu.xmu.privilege.model.bo.Role;
import cn.edu.xmu.privilege.model.po.*;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 角色访问类
 *
 * @author Ming Qiu
 * @date Created in 2020/11/1 11:48
 * Modified in 2020/11/3 12:16
 **/
@Repository
public class RoleDao implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(RoleDao.class);

    /**
     * 是否初始化，生成signature和加密
     */
    @Value("${prvilegeservice.initialization}")
    private Boolean initialization;

    @Value("${prvilegeservice.role.expiretime}")
    private long timeout;

    @Value("${prvilegeservice.randomtime}")
    private int randomTime;

    @Autowired
    private RolePoMapper roleMapper;

    @Autowired
    private UserRolePoMapper userRolePoMapper;

    @Autowired
    private RolePrivilegePoMapper rolePrivilegePoMapper;

    @Autowired
    private PrivilegeDao privDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 将一个角色的所有权限id载入到Redis
     *
     * @param id 角色id
     * @return void
     * createdBy: Ming Qiu 2020-11-02 11:44
     * ModifiedBy: Ming Qiu 2020-11-03 12:24
     * 将读取权限id的代码独立为getPrivIdsByRoleId. 增加redis值的有效期
     */
    public void loadRolePriv(Long id) {
        List<Long> privIds = this.getPrivIdsByRoleId(id);
        String key = "r_" + id;
        if (privIds.size() == 0){
            redisTemplate.opsForSet().add(key,"0");
        } else {
            for (Long pId : privIds) {
                redisTemplate.opsForSet().add(key, pId.toString());
            }
        }
        redisTemplate.expire(key, this.timeout + new Random().nextInt(randomTime), TimeUnit.SECONDS);
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
            StringBuilder signature = StringUtil.concatString("-", po.getRoleId().toString(),
                    po.getPrivilegeId().toString(), po.getCreatorId().toString());
            String newSignature = SHA256.getSHA256(signature.toString());

            if (newSignature.equals(po.getSignature())) {
                retIds.add(po.getPrivilegeId());
                logger.debug("getPrivIdsBByRoleId: roleId = " + po.getRoleId() + " privId = " + po.getPrivilegeId());
            } else {
                logger.error("getPrivIdsBByRoleId: Wrong Signature(auth_role_privilege): id =" + po.getId());
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
            StringBuilder signature = StringUtil.concatString("-", po.getRoleId().toString(),
                    po.getPrivilegeId().toString(), po.getCreatorId().toString());
            String newSignature = SHA256.getSHA256(signature.toString());
            RolePrivilegePo newPo = new RolePrivilegePo();
            newPo.setId(po.getId());
            newPo.setSignature(newSignature);
            rolePrivilegePoMapper.updateByPrimaryKeySelective(newPo);
        }

    }

    /**
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ReturnObject<List> selectAllRole(Integer pageNum, Integer pageSize) {
        RolePoExample example = new RolePoExample();
        RolePoExample.Criteria criteria = example.createCriteria();
        PageHelper.startPage(pageNum, pageSize);
        List<RolePo> rolePos = roleMapper.selectByExample(example);
        List<Role> roles = new ArrayList<>(rolePos.size());
        logger.info("selectAllRoles: total roles num = " + rolePos.size());
        for (RolePo rolePoItem : rolePos) {
            Role item = new Role(rolePoItem);
            roles.add(item);
        }
        return new ReturnObject<>(roles);
    }

    /**
     * @param role
     * @return
     */
    public ReturnObject<Role> insertRole(Role role) {
        RolePo rolePo = role.gotRolePo();
        RolePoExample example = new RolePoExample();
        RolePoExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(rolePo.getName());
        List<RolePo> rolePosByName = roleMapper.selectByExample(example);
        ReturnObject<Role> retObj = null;
        if (rolePosByName.size() > 0) {
            logger.info("insertRole: have same role name = " + rolePo.getName());
            retObj = new ReturnObject<>(ResponseCode.ROLE_REGISTERED);
        } else {
            int ret = roleMapper.insertSelective(rolePo);
            if (ret == 0) {
                logger.info("insertRole: id not exist = " + rolePo.getId());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                logger.info("insertRole: insert role = " + rolePo.toString());
                role.setId(rolePo.getId());
                retObj = new ReturnObject<>(role);
            }
        }
        return retObj;
    }

    /**
     * @param id
     * @return
     */
    public ReturnObject<Object> deleteRole(Long id) {
        ReturnObject<Object> retObj = null;
        int ret = roleMapper.deleteByPrimaryKey(id);
        if (ret == 0) {
            logger.info("deleteRole: id not exist = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("deleteRole: delete role id = " + id);
            RolePrivilegePoExample exampleRP = new RolePrivilegePoExample();
            RolePrivilegePoExample.Criteria criteriaRP = exampleRP.createCriteria();
            criteriaRP.andRoleIdEqualTo(id);
            List<RolePrivilegePo> rolePrivilegePos = rolePrivilegePoMapper.selectByExample(exampleRP);
            logger.info("deleteRole: delete role-privilege num = " + rolePrivilegePos.size());
            for (RolePrivilegePo rolePrivilegePo : rolePrivilegePos) {
                rolePrivilegePoMapper.deleteByPrimaryKey(rolePrivilegePo.getId());
            }

            UserRolePoExample exampleUR = new UserRolePoExample();
            UserRolePoExample.Criteria criteriaUR = exampleUR.createCriteria();
            criteriaUR.andRoleIdEqualTo(id);
            List<UserRolePo> userRolePos = userRolePoMapper.selectByExample(exampleUR);
            logger.info("deleteRole: delete user-role num = " + userRolePos.size());
            for (UserRolePo userRolePo : userRolePos) {
                userRolePoMapper.deleteByPrimaryKey(userRolePo.getId());
            }
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    /**
     * @param role
     * @return
     */
    public ReturnObject<Role> updateRole(Role role) {
        RolePo rolePo = role.gotRolePo();
        RolePoExample example = new RolePoExample();
        RolePoExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(rolePo.getName());
        List<RolePo> rolePosByName = roleMapper.selectByExample(example);
        ReturnObject<Role> retObj = null;
        if (rolePosByName.size() > 0) {
            logger.info("updateRole: have same role name = " + rolePo.getName());
            retObj = new ReturnObject<>(ResponseCode.ROLE_REGISTERED);
        } else {
            int ret = roleMapper.updateByPrimaryKeySelective(rolePo);
            if (ret == 0) {
                logger.info("updateRole: id not exist = " + rolePo.getId());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                logger.info("updateRole: update role = " + rolePo.toString());
                retObj = new ReturnObject<>();
            }
        }
        return retObj;
    }
}
