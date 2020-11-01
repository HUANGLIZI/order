package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.privilege.mapper.RolePoMapper;
import cn.edu.xmu.privilege.mapper.RolePrivilegePoMapper;
import cn.edu.xmu.privilege.model.po.RolePrivilegePo;
import cn.edu.xmu.privilege.model.po.RolePrivilegePoExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Ming Qiu
 **/
@Repository
public class RoleDao {

    @Autowired
    private RolePoMapper roleMapper;

    @Autowired
    private RolePrivilegePoMapper rolePrivilegePoMapper;

    @Autowired
    private PrivilegeDao privDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 将一个角色的所有权限id载入到Redis
     * @param id 角色id
     */
    public void loadRolePriv(Long id){
        String key = "r_" + id;
        RolePrivilegePoExample example = new RolePrivilegePoExample();
        RolePrivilegePoExample.Criteria criteria = example.createCriteria();
        criteria.andRoleIdEqualTo(id);
        List<RolePrivilegePo> rolePrivilegePos = rolePrivilegePoMapper.selectByExample(example);
        for (RolePrivilegePo po: rolePrivilegePos){
            redisTemplate.opsForSet().add(key, po.getId().toString());
        }
    }
}
