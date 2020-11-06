package cn.edu.xmu.privilege.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.dao.PrivilegeDao;
import cn.edu.xmu.privilege.dao.UserDao;
import cn.edu.xmu.privilege.model.bo.Privilege;
import cn.edu.xmu.privilege.model.bo.UserRole;
import cn.edu.xmu.privilege.model.bo.User;
import cn.edu.xmu.privilege.dao.UserDao;
import cn.edu.xmu.privilege.model.bo.User;
import cn.edu.xmu.privilege.model.vo.PrivilegeVo;
import cn.edu.xmu.privilege.util.ImgHelper;
import cn.edu.xmu.privilege.model.vo.UserEditVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * 用户服务
 * @author Ming Qiu
 * Modified at 2020/11/5 10:39
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
     * 取消用户角色
     * @param id 用户角色id
     * @return ReturnObject<VoObject>
     * @author Xianwei Wang
     * */
    @Transactional
    public ReturnObject<VoObject> revokeRole(Long id){
        return userDao.revokeRole(id);
    }

    /**
     * 赋予用户角色
     * @param createid 创建者id
     * @param userid 用户id
     * @param roleid 角色id
     * @return UserRole
     * @author Xianwei Wang
     * */
    @Transactional
    public ReturnObject<VoObject> assignRole(Long createid, Long userid, Long roleid){
        return userDao.assignRole(createid, userid, roleid);
    }

    /**
     * 查看用户的角色信息
     * @param id 用户id
     * @return 角色信息
     * @author Xianwei Wang
     * */
    @Transactional
    public ReturnObject<List> getSelfUserRoles(Long id){
        return userDao.getUserRoles(id);
    }


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

        if(userReturnObject.getCode() == ResponseCode.RESOURCE_ID_NOTEXIST)
            return userReturnObject;
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
