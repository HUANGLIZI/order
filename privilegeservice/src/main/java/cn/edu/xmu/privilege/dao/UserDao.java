package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.privilege.mapper.UserProxyPoMapper;
import cn.edu.xmu.privilege.mapper.UserRolePoMapper;
import cn.edu.xmu.privilege.model.po.UserProxyPo;
import cn.edu.xmu.privilege.model.po.UserProxyPoExample;
import cn.edu.xmu.privilege.model.po.UserRolePo;
import cn.edu.xmu.privilege.model.po.UserRolePoExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @author Ming Qiu
 **/
@Repository
public class UserDao {

    @Autowired
    private UserRolePoMapper userRolePoMapper;

    @Autowired
    private UserProxyPoMapper userProxyPoMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RoleDao roleDao;

    /**
     *计算User自己的权限，load到Redis
     * @param id userID
     */
    private void loadSingleUserPriv(Long id){
        String key = "u_"+id;
        UserRolePoExample example = new UserRolePoExample();
        UserRolePoExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(id);
        List<UserRolePo> userRolePoList = userRolePoMapper.selectByExample(example);

        Set<String> roleKeys = new HashSet<>(userRolePoList.size());
        for (UserRolePo po: userRolePoList) {
            String roleKey = "r_" + po.getRoleId();
            roleKeys.add(roleKey);
            if (!redisTemplate.hasKey(roleKey)) {
                roleDao.loadRolePriv(po.getRoleId());
            }
        }
        redisTemplate.opsForSet().unionAndStore(roleKeys, key);
    }

    /**
     *计算User的权限（包括代理用户的权限，只计算直接代理用户），load到Redis
     * @param id userID
     */
    public void loadUserPriv(Long id){

        String key = "u_"+id;
        String aKey = "up_"+id;
        UserProxyPoExample example = new UserProxyPoExample();
        UserProxyPoExample.Criteria criteria = example.createCriteria();
        criteria.andUserAIdEqualTo(id);
        List<UserProxyPo> userProxyPos = userProxyPoMapper.selectByExample(example);
        List<String> proxyUserKey = new ArrayList<>(userProxyPos.size());
        for (UserProxyPo po: userProxyPos){
            if (!redisTemplate.hasKey("u_"+po.getUserBId())) {
                loadUserPriv(po.getUserBId());
            }
            proxyUserKey.add("u_"+po.getUserBId());
        }
        if (!redisTemplate.hasKey(key)) {
            loadUserPriv(id);
        }
        redisTemplate.opsForSet().unionAndStore(key, proxyUserKey, aKey);
    }
}
