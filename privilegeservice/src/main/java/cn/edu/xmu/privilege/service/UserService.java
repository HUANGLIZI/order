package cn.edu.xmu.privilege.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.model.bo.User;
import cn.edu.xmu.privilege.dao.PrivilegeDao;
import cn.edu.xmu.privilege.model.vo.PrivilegeVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.edu.xmu.privilege.dao.UserDao;
import cn.edu.xmu.privilege.model.po.UserPo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用户服务
 * @author Ming Qiu
 **/
@Service
public class UserService {
    private Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserDao userDao;

    /**
     * 获取所有用户信息
     * @author XQChen
     * @param id
     * @return 用户列表
     */
    public ReturnObject<VoObject> findUserById(Long id) {
        ReturnObject<VoObject> returnObject = null;

        UserPo userPo = userDao.findUserById(id);
        if(userPo != null) {
            returnObject = new ReturnObject<>(new User(userPo));
        } else {
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        return returnObject;
    }

    /**
     * 获取所有用户信息
     * @author XQChen
     * @param userName
     * @param mobile
     * @param page
     * @param pagesize
     * @return 用户列表
     */
    public ReturnObject<List> findAllUsers(String userName, String mobile, Integer page, Integer pagesize) {

        List<UserPo> userPos = userDao.findAllUsers();

        Stream<UserPo> userPoStream = userPos.stream();
        if(!(userName == null) && !userName.isBlank())
            userPoStream = userPoStream.filter(userPo -> (userPo.getUserName().contentEquals(userName)));
        if(!(mobile == null) && !mobile.isBlank())
            userPoStream = userPoStream.filter(userPo -> (userPo.getMobile().contentEquals(mobile)));

        List<UserPo> tmpUserPos = userPoStream.collect(Collectors.toList());

        Integer size = tmpUserPos.size();

        userPoStream = tmpUserPos.stream();

        if(page * pagesize <= size)
        {
            userPoStream = userPoStream.skip((page - 1) * pagesize).limit(pagesize);
        } else {
            Integer returnSize = size < pagesize ? size : pagesize;
            userPoStream = userPoStream.skip(size - returnSize).limit(returnSize);
        }

        ReturnObject<List> returnObject = new ReturnObject<>(userPoStream.map(User::new).collect(Collectors.toList()));

        return returnObject;
    }

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
}
