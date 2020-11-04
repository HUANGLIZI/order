package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.ooad.util.SHA256;
import cn.edu.xmu.ooad.util.StringUtil;
import cn.edu.xmu.privilege.mapper.RolePoMapper;
import cn.edu.xmu.privilege.mapper.RolePrivilegePoMapper;
import cn.edu.xmu.privilege.model.bo.Privilege;
import cn.edu.xmu.privilege.model.bo.Role;
import cn.edu.xmu.privilege.model.po.RolePo;
import cn.edu.xmu.privilege.model.po.RolePrivilegePo;
import cn.edu.xmu.privilege.model.po.RolePrivilegePoExample;
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
    private RolePrivilegePoMapper rolePrivilegePoMapper;

    @Autowired
    private PrivilegeDao privDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * Role通过自己的id找到对应的privIds, 根据privDao获取权限
     *
     * @param id roleID
     * @return List<Privilege>
     * 将获取用户所有权限
     */
    public List<Privilege> findPrivsByRoleId(Long id) {
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
}
