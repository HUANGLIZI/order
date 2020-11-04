package cn.edu.xmu.privilege.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ImgHelper;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.dao.PrivilegeDao;
import cn.edu.xmu.privilege.dao.UserDao;
import cn.edu.xmu.privilege.model.bo.Privilege;
import cn.edu.xmu.privilege.model.bo.User;
import cn.edu.xmu.privilege.model.vo.PrivilegeVo;
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
    @Value("${prvilegeservice.imglocation}")
    private String imgLocation;

    @Autowired
    UserDao userDao;


    @Autowired
    private PrivilegeDao privilegeDao;

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
     * 上传图片
     * @author 3218
     * @param id: 用户id
     * @param multipartFile: 文件
     * @return
     */
    public ReturnObject uploadImg(Integer id, MultipartFile multipartFile){
        User user = userDao.getUserById(id);

        try{
            String fileName = ImgHelper.saveImg(multipartFile,imgLocation,id);
            if(user.getAvatar()==null){
                user.setAvatar(fileName);
                userDao.updateUserAvatar(user);
            }
            else{
                String oldFilename = user.getAvatar();
                //已经有相同文件则直接返回
                if(oldFilename.equals(fileName))
                    return new ReturnObject();
                ImgHelper.deleteImg(oldFilename,imgLocation);
                user.setAvatar(fileName);
                userDao.updateUserAvatar(user);
            }
        }
        catch (IOException e){
            e.printStackTrace();
            //暂无错误码
            return new ReturnObject();
        }
        return new ReturnObject();
    }
}
