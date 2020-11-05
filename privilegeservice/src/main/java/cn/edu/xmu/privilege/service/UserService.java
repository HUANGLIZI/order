package cn.edu.xmu.privilege.service;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.dao.PrivilegeDao;
import cn.edu.xmu.privilege.dao.UserDao;
import cn.edu.xmu.privilege.model.bo.User;
import cn.edu.xmu.privilege.dao.UserDao;
import cn.edu.xmu.privilege.model.vo.PrivilegeVo;
import cn.edu.xmu.privilege.util.ImgHelper;
import cn.edu.xmu.privilege.model.vo.UserEditVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 用户服务
 * @author Ming Qiu
 **/
@Service
public class UserService {
    private Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * @author 24320182203218
     **/
    @Value("${prvilegeservice.imgloaction}")
    private String imgLocation;

    @Autowired
    private PrivilegeDao privilegeDao;

    @Autowired
    private UserDao userDao;

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

    /**
     * 根据 ID 和 UserEditVo 修改任意用户信息
     * @param id 用户 id
     * @param vo UserEditVo 对象
     * @return 返回对象 ReturnObject
     */
    public ReturnObject<Object> modifyUserInfo(Long id, UserEditVo vo) {
        return userDao.modifyUserByVo(id, vo);
    }

    /**
     * 根据 id 删除任意用户
     * @param id 用户 id
     * @return 返回对象 ReturnObject
     */
    public ReturnObject<Object> deleteUser(Long id) {
        // 注：逻辑删除
        return userDao.logicallyDeleteUser(id);
    }

    /**
     * 根据 id 禁止任意用户登录
     * @param id 用户 id
     * @return 返回对象 ReturnObject
     */
    public ReturnObject<Object> forbidUser(Long id) {
        return userDao.forbidUser(id);
    }

    /**
     * 解禁任意用户
     * @param id 用户 id
     * @return 返回对象 ReturnObject
     */
    public ReturnObject<Object> releaseUser(Long id) {
        return userDao.releaseUser(id);
    }

    /**
     * 上传图片
     * @author 3218
     * @param id: 用户id
     * @param multipartFile: 文件
     * @return
     */
    public ReturnObject uploadImg(Integer id, MultipartFile multipartFile){
        User user = userDao.getUserById(id);

        ReturnObject object = new ReturnObject(ResponseCode.OK);
        try{
            if(user.getAvatar()==null){
                object = ImgHelper.saveImg(multipartFile,imgLocation);
                if(object.getErrmsg().equals(ResponseCode.FILE_NO_WRITE_PERMISSION.getMessage())){
                    logger.debug(object.getErrmsg());
                    throw new IOException();
                }
                user.setAvatar(object.getData().toString());
                userDao.updateUserAvatar(user);
            }
            else{
                String oldFilename = user.getAvatar();
                ImgHelper.deleteImg(oldFilename,imgLocation);
                object = ImgHelper.saveImg(multipartFile,imgLocation);
                if(object.getErrmsg().equals(ResponseCode.FILE_NO_WRITE_PERMISSION.getMessage())){
                    logger.debug(object.getErrmsg());
                    throw new IOException();
                }
                user.setAvatar(object.getData().toString());
                userDao.updateUserAvatar(user);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return object;
    }
}
