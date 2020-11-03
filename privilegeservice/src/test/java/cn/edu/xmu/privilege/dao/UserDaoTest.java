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
 * @date Created in 2020/11/3 20:41
 **/
@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@Transactional
public class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void loadUserPriv1(){

        userDao.loadUserPriv(Long.valueOf(1));

        String key1 = "u_1";
        String key2 = "up_1";

        assertTrue(redisTemplate.hasKey(key1));
        assertTrue(redisTemplate.opsForSet().isMember(key1,"2"));
        assertTrue(redisTemplate.opsForSet().isMember(key1,"3"));
        assertTrue(redisTemplate.opsForSet().isMember(key1,"4"));
        assertTrue(redisTemplate.opsForSet().isMember(key1,"5"));
        assertTrue(redisTemplate.opsForSet().isMember(key1,"6"));
        assertTrue(redisTemplate.opsForSet().isMember(key1,"7"));
        assertTrue(redisTemplate.opsForSet().isMember(key1,"8"));
        assertTrue(redisTemplate.opsForSet().isMember(key1,"9"));
        assertFalse(redisTemplate.opsForSet().isMember(key1,"1"));
        assertEquals(16, redisTemplate.opsForSet().size(key1));

        assertTrue(redisTemplate.hasKey(key2));
        assertTrue(redisTemplate.opsForSet().isMember(key2,"2"));
        assertTrue(redisTemplate.opsForSet().isMember(key2,"3"));
        assertTrue(redisTemplate.opsForSet().isMember(key2,"4"));
        assertTrue(redisTemplate.opsForSet().isMember(key2,"5"));
        assertTrue(redisTemplate.opsForSet().isMember(key2,"6"));
        assertTrue(redisTemplate.opsForSet().isMember(key2,"7"));
        assertTrue(redisTemplate.opsForSet().isMember(key2,"8"));
        assertTrue(redisTemplate.opsForSet().isMember(key2,"9"));
        assertFalse(redisTemplate.opsForSet().isMember(key2,"1"));
        assertEquals(16, redisTemplate.opsForSet().size(key2));

    }

    @Test
    public void loadUserPriv2(){

        userDao.loadUserPriv(Long.valueOf(46));

        String key1 = "u_46";
        String key2 = "up_46";

        assertTrue(redisTemplate.hasKey(key1));
        assertTrue(redisTemplate.opsForSet().isMember(key1,"2"));
        assertTrue(redisTemplate.opsForSet().isMember(key1,"15"));
        assertTrue(redisTemplate.opsForSet().isMember(key1,"16"));
        assertFalse(redisTemplate.opsForSet().isMember(key1,"1"));
        assertEquals(3, redisTemplate.opsForSet().size(key1));

        assertTrue(redisTemplate.hasKey(key2));
        assertTrue(redisTemplate.opsForSet().isMember(key2,"2"));
        assertTrue(redisTemplate.opsForSet().isMember(key2,"15"));
        assertTrue(redisTemplate.opsForSet().isMember(key2,"16"));
        assertFalse(redisTemplate.opsForSet().isMember(key2,"1"));
        assertEquals(3, redisTemplate.opsForSet().size(key2));
    }

}
