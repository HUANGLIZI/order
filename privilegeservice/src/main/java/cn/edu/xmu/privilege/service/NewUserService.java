package cn.edu.xmu.privilege.service;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.privilege.dao.NewUserDao;
import cn.edu.xmu.privilege.dao.UserDao;
import cn.edu.xmu.privilege.model.bo.User;
import cn.edu.xmu.privilege.model.po.NewUserPo;
import cn.edu.xmu.privilege.model.vo.NewUserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 新用户服务
 * @author LiangJi3229
 * @date 2020/11/10 18:41
 */
@Service
public class NewUserService {
    private Logger logger = LoggerFactory.getLogger(NewUserService.class);

    @Autowired
    NewUserDao newUserDao;

    /**
     * @param vo 注册的vo对象
     * @return ReturnObject
     * @author LiangJi3229
     */
    @Transactional
    public ReturnObject register(NewUserVo vo) {
        return newUserDao.createNewUserByVo(vo);
    }
    
    /**
     * 新用户审核未通过，删除
     * @param id
     * @return ReturnObject
     * @author 24320182203227 Li Zihan
     */
    @Transactional
    public ReturnObject deleteNewUser(Long id) {
        return newUserDao.physicallyDeleteUser(id);
    }

    /**
     * 根据id查找新用户
     * @param id
     * @return NewUserPo
     * @author 24320182203227 Li Zihan
     */
    @Transactional
    public NewUserPo findNewUser(Long id) {
        return newUserDao.findNewUserById(id);
    }

}
