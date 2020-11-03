package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ming Qiu
 * @date Created in 2020/11/3 15:23
 **/
@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@Transactional
public class RoleDaoTest {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void loadRolePriv1(){

        roleDao.loadRolePriv(Long.valueOf(23));

        String key = "r_23";
        assertTrue(redisTemplate.hasKey(key));
        assertTrue(redisTemplate.opsForSet().isMember(key,"2"));
        assertTrue(redisTemplate.opsForSet().isMember(key,"3"));
        assertTrue(redisTemplate.opsForSet().isMember(key,"4"));
        assertTrue(redisTemplate.opsForSet().isMember(key,"5"));
        assertFalse(redisTemplate.opsForSet().isMember(key,"1"));
        assertEquals(16, redisTemplate.opsForSet().size(key));
    }

    @Test
    public void loadRolePriv2(){
        roleDao.loadRolePriv(Long.valueOf(80));
        assertFalse(redisTemplate.hasKey("r_80"));
    }

    @Test
    public void loadRolePriv3(){

        roleDao.loadRolePriv(Long.valueOf(84));

        String key = "r_84";
        assertTrue(redisTemplate.hasKey(key));
        assertTrue(redisTemplate.opsForSet().isMember(key,"14"));
        assertFalse(redisTemplate.opsForSet().isMember(key,"2"));
        assertEquals(1, redisTemplate.opsForSet().size(key));

    }

}
