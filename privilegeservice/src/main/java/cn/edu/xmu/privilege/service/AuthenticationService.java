package cn.edu.xmu.privilege.service;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.dao.UserDao;
import cn.edu.xmu.privilege.model.bo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserDao userDao;

//    @Autowired
    private JwtHelper jwtHelper = new JwtHelper();

    @Autowired
    private RedisTemplate <String, String> redisTemplate;

    public ReturnObject<String> Login(String username, String password)
    {
        ReturnObject<String> retObj = null;
        User user = userDao.getUserByName(username);

        if(user.getPassword().equals(password)){
            String jwt = jwtHelper.createToken(user.getId(),user.getDepartId());
            retObj = new ReturnObject<>(jwt);
        } else {
            retObj = new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT, "用户名或密码错误");
        }
        return retObj;
    }

    public ReturnObject<Boolean> Logout(Long userId)
    {
        ReturnObject<Boolean> retObj = null;
        Long delCount = redisTemplate.opsForSet().remove("u_" + userId.toString());
        if (delCount>0){
            retObj = new ReturnObject<>(ResponseCode.AUTH_ID_NOTEXIST,"注销失败");
        } else {
            retObj = new ReturnObject<>(true);
        }
        return retObj;
    }
}
