package cn.edu.xmu.privilege.service;

import cn.edu.xmu.ooad.util.AES;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.dao.UserDao;
import cn.edu.xmu.privilege.model.bo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthenticationService {

    @Autowired
    private UserDao userDao;

    @Value("${loginconfig.multiply}")
    private Boolean canMultiplyLogin;

//    @Autowired
    private JwtHelper jwtHelper = new JwtHelper();

    @Autowired
    private RedisTemplate <String, String> redisTemplate;

    public ReturnObject<String> Login(String username, String password, String IpAddr)
    {
        ReturnObject<String> retObj = null;
        User user = userDao.getUserByName(username);

        if(user == null || !password.equals(AES.decrypt(user.getPassword(), User.AESPASS))){
            retObj = new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT, "用户名或密码错误");
            return retObj;
        }
        if (user.getState() != User.State.NORM){
            retObj = new ReturnObject<>(ResponseCode.AUTH_USER_FORBIDDEN, "您的状态为" + user.getState().getDescription());
            return retObj;
        }
        if(redisTemplate.hasKey(user.getId().toString()) && canMultiplyLogin == false){
            // 用户重复登录处理
        }

        String jwt = jwtHelper.createToken(user.getId(),user.getDepartId());
        userDao.loadUserPriv(user.getId());
        userDao.setLoginIPAndPosition(user.getId(),IpAddr, LocalDateTime.now());
        retObj = new ReturnObject<>(jwt);

        return retObj;
    }

    public ReturnObject<Boolean> Logout(Long userId)
    {
        ReturnObject<Boolean> retObj = null;
        Boolean success = redisTemplate.delete("u_" + userId);
        if (success){
            retObj = new ReturnObject<>(true);
        } else {
            retObj = new ReturnObject<>(ResponseCode.AUTH_ID_NOTEXIST,"您尚未登录，无需注销");
        }
        return retObj;
    }
}
