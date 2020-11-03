package cn.edu.xmu.privilege.service;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.privilege.dao.UserDao;
import cn.edu.xmu.privilege.model.bo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private RedisTemplate <String, String> redisTemplate;

    public String Login(String username, String password)
    {
        User user = userDao.getUserByName(username);

        if(user.getPassword().equals(password)){
            String jwt = jwtHelper.createToken(user.getId(),user.getDepartId());
            return jwt;
        } else {
            return null;
        }
    }

    public Boolean Logout(Long userId)
    {
        Long delCount = redisTemplate.opsForSet().remove("u_" + userId.toString());
        if (delCount>0){
            return true;
        } else {
            return false;
        }
    }
}
