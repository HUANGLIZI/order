package cn.edu.xmu.privilege.service;

import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.dao.PrivilegeDao;
import cn.edu.xmu.privilege.dao.UserDao;
import cn.edu.xmu.privilege.model.bo.User;
import cn.edu.xmu.privilege.model.vo.PrivilegeVo;
import cn.edu.xmu.privilege.util.ImgHelper;
import cn.edu.xmu.privilege.model.vo.UserEditVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务
 * @author Ming Qiu
 * Modified at 2020/11/5 10:39
 **/
@Service
public class UserService {
    private Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${privilegeservice.login.jwtExpire}")
    private Integer jwtExpireTime;

    /**
     * @author 24320182203218
     **/
    @Value("${privilegeservice.imglocation}")
    private String imgLocation;

    @Autowired
    private PrivilegeDao privilegeDao;

    @Autowired
    private UserDao userDao;

    @Value("${privilegeservice.login.multiply}")
    private Boolean canMultiplyLogin;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 分布式锁的过期时间（秒）
     */
    @Value("${privilegeservice.lockerExpireTime}")
    private long lockerExpireTime;

    /**
     * 查询所有权限
     * @return 权限列表
     */
    public ReturnObject<List> findAllPrivs(){
        ReturnObject<List>  ret = new ReturnObject<>(privilegeDao.findAllPrivs());
        return ret;
    }

    /**
     * 修改权限
     * @param id: 权限id
     * @return
     */
    public ReturnObject changePriv(Long id, PrivilegeVo vo){
        return privilegeDao.changePriv(id, vo);
    }

    @Transactional
    public ReturnObject login(String userName, String password, String ipAddr)
    {
        ReturnObject retObj = userDao.getUserByName(userName);
        if (retObj.getCode() != ResponseCode.OK){
            return retObj;
        }

        User user = (User) retObj.getData();
        password = AES.encrypt(password, User.AESPASS);
        if(user == null || !password.equals(user.getPassword())){
            logger.debug("login: user = "+user +" password = "+password );
            retObj = new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT);
            return retObj;
        }
        if (user.getState() != User.State.NORM){
            retObj = new ReturnObject<>(ResponseCode.AUTH_USER_FORBIDDEN);
            return retObj;
        }
        if (!user.authetic()){
            retObj = new ReturnObject<>(ResponseCode.AUTH_USER_FORBIDDEN, "信息被篡改");
            StringBuilder message = new StringBuilder().append("Login: userid = ").append(user.getId()).
                    append(", username =").append(user.getUserName()).append(" 信息被篡改");
            logger.error(message.toString());
            return retObj;
        }

        String key = "up_" + user.getId();
        if(redisTemplate.hasKey(key) && canMultiplyLogin == false){
            // 用户重复登录处理
            Set<Serializable > set = redisTemplate.opsForSet().members(key);
            redisTemplate.delete(key);

            /* 将旧JWT加入需要踢出的集合 */
            String jwt = null;
            for (Serializable str : set) {
                /* 找出JWT */
                if(((String)str).length() > 8){
                    jwt = (String) str;
                    break;
                }
            }
            this.banJwt(jwt);
        }

        //创建新的token
        JwtHelper jwtHelper = new JwtHelper();
        String jwt = jwtHelper.createToken(user.getId(),user.getDepartId(), jwtExpireTime);
        logger.debug("login: jwt ="+ jwt);
        userDao.loadUserPriv(user.getId(), jwt);
        userDao.setLoginIPAndPosition(user.getId(),ipAddr, LocalDateTime.now());
        retObj = new ReturnObject<>(jwt);

        return retObj;
    }

    /**
     * 禁止持有特定令牌的用户登录
     * @param jwt JWT令牌
     */
    private void banJwt(String jwt){
        String[] banSetName = {"BanJwt_0", "BanJwt_1"};
        long bannIndex = 0;

        if (!redisTemplate.hasKey("banIndex")){
            redisTemplate.opsForValue().set("banIndex", "0");
        } else {
            bannIndex = Long.parseLong(redisTemplate.opsForValue().get("banIndex").toString());
        }

        String currentSetName = banSetName[(int) (bannIndex % banSetName.length)];

        if(!redisTemplate.hasKey(currentSetName)) {
            // 新建
            redisTemplate.opsForSet().add(currentSetName, jwt);
            redisTemplate.expire(currentSetName,jwtExpireTime * 2,TimeUnit.SECONDS);
        }else{
            //准备向其中添加元素
            if(redisTemplate.getExpire(currentSetName, TimeUnit.SECONDS) > jwtExpireTime) {
                // 有效期还长，直接加入
                redisTemplate.opsForSet().add(currentSetName, jwt);
            } else {
                // 有效期不够JWT的过期时间，准备用第二集合，让第一个集合自然过期
                // 分步式加锁
                while (!redisTemplate.opsForValue().setIfAbsent("banIndexLocker","nouse", lockerExpireTime, TimeUnit.SECONDS)){
                    try {
                        Thread.sleep(10);
                    }catch (InterruptedException e){
                        logger.error("banJwt: 锁等待被打断");
                    }
                    catch (IllegalArgumentException e){

                    }
                }
                bannIndex = redisTemplate.opsForValue().increment("banIndex");
                currentSetName = banSetName[(int) (bannIndex % banSetName.length)];
                //启用之前，不管有没有，先删除一下，应该是没有，保险起见
                redisTemplate.delete(currentSetName);
                redisTemplate.opsForSet().add(currentSetName, jwt);
                redisTemplate.expire(currentSetName,jwtExpireTime * 2,TimeUnit.SECONDS);
                // 解锁
                redisTemplate.delete("banIndexLocker");
            }
        }
    }


    public ReturnObject<Boolean> Logout(Long userId)
    {
        redisTemplate.delete("up_" + userId);
        return new ReturnObject<>(true);
    }

    /**
     * 根据 ID 和 UserEditVo 修改任意用户信息
     * @param id 用户 id
     * @param vo UserEditVo 对象
     * @return 返回对象 ReturnObject
     */
    @Transactional
    public ReturnObject<Object> modifyUserInfo(Long id, UserEditVo vo) {
        return userDao.modifyUserByVo(id, vo);
    }

    /**
     * 根据 id 删除任意用户
     * @param id 用户 id
     * @return 返回对象 ReturnObject
     */
    @Transactional
    public ReturnObject<Object> deleteUser(Long id) {
        // 注：逻辑删除
        return userDao.changeUserState(id, User.State.DELETE);
    }

    /**
     * 根据 id 禁止任意用户登录
     * @param id 用户 id
     * @return 返回对象 ReturnObject
     */
    @Transactional
    public ReturnObject<Object> forbidUser(Long id) {
        return userDao.changeUserState(id, User.State.FORBID);
    }

    /**
     * 解禁任意用户
     * @param id 用户 id
     * @return 返回对象 ReturnObject
     */
    @Transactional
    public ReturnObject<Object> releaseUser(Long id) {
        return userDao.changeUserState(id, User.State.NORM);
    }

    /**
     * 上传图片
     * @author 3218
     * @param id: 用户id
     * @param multipartFile: 文件
     * @return
     */
    @Transactional
    public ReturnObject uploadImg(Integer id, MultipartFile multipartFile){
        ReturnObject<User> userReturnObject = userDao.getUserById(id);

        if(userReturnObject.getCode() == ResponseCode.RESOURCE_ID_NOTEXIST) {
            return userReturnObject;
        }
        User user = userReturnObject.getData();

        ReturnObject returnObject = new ReturnObject();
        try{
            returnObject = ImgHelper.saveImg(multipartFile,imgLocation);
            //无写入权限
            if(returnObject.getCode() == ResponseCode.FILE_NO_WRITE_PERMISSION){
                logger.debug(returnObject.getErrmsg());
                return returnObject;
            }

            String oldFilename = user.getAvatar();
            user.setAvatar(returnObject.getData().toString());
            ReturnObject updateReturnObject = userDao.updateUserAvatar(user);

            //数据库更新失败，需删除新增的图片
            if(updateReturnObject.getCode()==ResponseCode.FIELD_NOTVALID){
                ImgHelper.deleteImg(returnObject.getData().toString(),imgLocation);
                return updateReturnObject;
            }

            //数据库更新成功需删除旧图片，未设置则不删除
            if(oldFilename!=null) {
                ImgHelper.deleteImg(oldFilename, imgLocation);
            }
        }
        catch (IOException e){
            logger.debug("uploadImg: I/O Error:" + imgLocation);
        }
        return returnObject;
    }
}
