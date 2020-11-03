package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.SHA256;
import cn.edu.xmu.privilege.mapper.PrivilegeMapper;
import cn.edu.xmu.privilege.mapper.PrivilegePoMapper;
import cn.edu.xmu.privilege.model.bo.Privilege;
import cn.edu.xmu.privilege.model.po.PrivilegePo;
import cn.edu.xmu.privilege.model.po.PrivilegePoExample;
import cn.edu.xmu.privilege.model.vo.PrivilegeVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限DAO
 * @author Ming Qiu
 **/
@Repository
public class PrivilegeDao implements InitializingBean {

    private  static  final Logger logger = LoggerFactory.getLogger(PrivilegeDao.class);

    /**
     * 是否初始化，生成signature和加密
     */
    @Value("${prvilegeservice.initialization}")
    private Boolean initialization;

    @Autowired
    private PrivilegeMapper mapper;

    @Autowired
    private PrivilegePoMapper poMapper;

    private Map<String, Long> cache = null;

    /**
     * 将权限载入到本地缓存中
     * 如果未初始化，则初始话数据中的数据
     * @throws Exception
     * createdBy: Ming Qiu 2020-11-01 23:44
     * modifiedBy: Ming Qiu 2020-11-03 11:44
     *            将签名的认证改到Privilege对象中去完成
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<PrivilegePo> privilegePos =  mapper.selectAll();
        if (null == cache){
            cache = new HashMap<>(privilegePos.size());
        }
        for (PrivilegePo po : privilegePos){
            Privilege priv = new Privilege(po);
            if (null == po.getSignature() && initialization){
                PrivilegePo newPo = new PrivilegePo();
                newPo.setId(po.getId());
                newPo.setSignature(priv.getCacuSignature());
                newPo.setGmtModified(LocalDateTime.now());
                poMapper.updateByPrimaryKeySelective(newPo);
            }else {
                if (priv.authetic()) {
                    logger.debug("afterPropertiesSet: key = " + priv.getKey() + " p = " + priv);
                    cache.put(priv.getKey(), priv.getId());
                }else{
                    logger.error("afterPropertiesSet: Wrong Signature(auth_privilege): id = " + priv.getId());
                }
            }
        }
    }

    /**
     * 以url和RequestType获得缓存的Privilege id
     * @param url: 访问链接
     * @param requestType: 访问类型
     * @return id Privilege id
     * createdBy: Ming Qiu 2020-11-01 23:44
     */
    public Long getPrivIdByKey(String url, Privilege.RequestType requestType){
        StringBuffer key = new StringBuffer(url);
        key.append("-");
        key.append(requestType.getCode());
        logger.info("getPrivIdByKey: key = "+key.toString());
        return this.cache.get(key.toString());
    }

    /**
     * 查询所有权限
     * @return 权限列表
     */
    public List<Privilege> findAllPrivs(){
        List<PrivilegePo> privilegePos = mapper.selectAll();
        List<Privilege> ret = new ArrayList<>(privilegePos.size());
        for (PrivilegePo po : privilegePos) {
            Privilege priv = new Privilege(po);
            if (priv.authetic()) {
                logger.debug("afterPropertiesSet: key = " + priv.getKey() + " p = " + priv);
                ret.add(priv);
            }
        }
        return ret;
    }

    /**
     * 修改权限
     * @param id: 权限id
     * @return ReturnObject
     */
    public ReturnObject changePriv(Long id, PrivilegeVo vo){
        PrivilegePo po = this.poMapper.selectByPrimaryKey(id);
        logger.debug("changePriv: vo = "+ vo  + " po = "+ po);
        Privilege privilege = new Privilege(po);
        PrivilegePo newPo = privilege.createUpdatePo(vo);
        this.poMapper.updateByPrimaryKeySelective(newPo);
        return new ReturnObject();
    }

}
