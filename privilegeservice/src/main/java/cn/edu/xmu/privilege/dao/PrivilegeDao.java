package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.ooad.util.SHA256;
import cn.edu.xmu.privilege.mapper.PrivilegeMapper;
import cn.edu.xmu.privilege.mapper.PrivilegePoMapper;
import cn.edu.xmu.privilege.model.bo.Privilege;
import cn.edu.xmu.privilege.model.po.PrivilegePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限DAO
 * @author Ming Qiu
 **/
@Repository
public class PrivilegeDao implements InitializingBean {

    private  static  final Logger logger = LoggerFactory.getLogger(Privilege.class);

    @Value("${prvilegeservice.initialization}")
    private Boolean initialization;

    @Autowired
    private PrivilegeMapper mapper;

    @Autowired
    private PrivilegePoMapper poMapper;

    private Map<String, Long> cache = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<PrivilegePo> privilegePos =  mapper.selectAll();
        if (null == cache){
            cache = new HashMap<>(privilegePos.size());
        }
        for (PrivilegePo po : privilegePos){
            StringBuffer signature = new StringBuffer();
            signature.append(po.getUrl());
            signature.append("-");
            signature.append(po.getRequestType());
            String key = signature.toString();

            signature.append("-");
            signature.append(po.getId());
            String newSignature = SHA256.getSHA256(signature.toString());
            if (null == po.getSignature() && initialization){
                PrivilegePo newPo = new PrivilegePo();
                newPo.setId(po.getId());
                newPo.setSignature(newSignature);
                newPo.setGmtModified(LocalDateTime.now());
                poMapper.updateByPrimaryKeySelective(newPo);
            }else if (po.getSignature() != newSignature){
                continue;
            }
            logger.info("afterPropertiesSet: key = "+key+" po = "+po);
            cache.put(key, po.getId());
        }
    }

    /**
     * 以url和RequestType获得缓存的Privilege id
     * @param url: 访问链接
     * @param requestType: 访问类型
     * @return id
     */
    public Long getPrivIdByKey(String url, Privilege.RequestType requestType){
        StringBuffer key = new StringBuffer(url);
        key.append("-");
        key.append(requestType.getCode());
        logger.info("getPrivIdByKey: key = "+key.toString());
        return this.cache.get(key.toString());
    }

}
