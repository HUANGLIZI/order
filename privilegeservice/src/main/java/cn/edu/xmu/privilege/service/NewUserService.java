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
     * auth014: 管理员审核新用户
     * @param id
     * @param approve
     * @author 24320182203227 Li Zihan
     */
    @Transactional
    public ReturnObject<Object> approveUser(Long id,Long did,Boolean approve)
    {
        ReturnObject returnObject=null;
        if(approve==true && did==0)
        {
            NewUserPo newUserPo=newUserDao.findNewUserById(id);
            returnObject=userDao.addUser(newUserPo);
            newUserDao.physicallyDeleteUser(id);
        }
        else if(approve==true && did!=0)
        {
            NewUserPo newUserPo=newUserDao.findNewUserById(id);
            if(newUserPo.getDepartId()==did)
            {
                returnObject=userDao.addUser(newUserPo);
                newUserDao.physicallyDeleteUser(id);
            }
            else
            {
                logger.debug("did未匹配");
                return returnObject;
            }
        }
        else if(approve==false && did==0)
        {
            returnObject=newUserDao.physicallyDeleteUser(id);
        }
        else if(approve==false && did!=0)
        {
            NewUserPo newUserPo=newUserDao.findNewUserById(id);
            if(newUserPo.getDepartId()==did)
            {
                returnObject=newUserDao.physicallyDeleteUser(id);
            }
            else
            {
                logger.debug("did未匹配");
                return returnObject;
            }
        }
        return returnObject;
    }

}
