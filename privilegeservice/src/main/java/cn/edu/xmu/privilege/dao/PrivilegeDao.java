package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.ooad.util.SHA256;
import cn.edu.xmu.privilege.mapper.PrivilegeMapper;
import cn.edu.xmu.privilege.model.bo.Privilege;
import cn.edu.xmu.privilege.model.bo.PrivilegeBrief;
import cn.edu.xmu.privilege.model.po.PrivilegePo;
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

    @Value("${prvilegeservice.service}")
    private Boolean initialization;

    @Autowired
    private PrivilegeMapper mapper;

    private Map<Long, PrivilegeBrief> idCache = null;
    private Map<String, PrivilegeBrief> keyCache = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<PrivilegePo> privilegePos =  mapper.selectAll();
        if (null == idCache){
            idCache = new HashMap<>(privilegePos.size());
        }
        if (null == keyCache){
            keyCache = new HashMap<>(privilegePos.size());
        }
        StringBuffer signature = new StringBuffer();
        for (PrivilegePo po : privilegePos){

            signature.append(po.getUrl());
            signature.append("-");
            signature.append(po.getRequestType());
            String key = signature.toString();

            signature.append("-");
            signature.append(po.getBitIndex());
            String newSignature = SHA256.getSHA256(signature.toString());
            if (null == po.getSignature() && initialization){
                PrivilegePo newPo = new PrivilegePo();
                newPo.setId(po.getId());
                newPo.setSignature(newSignature);
                newPo.setGmtModified(LocalDateTime.now());
                mapper.updateByPrimaryKeySelective(newPo);
            }else if (po.getSignature() != newSignature){
                continue;
            }

            PrivilegeBrief priv = new PrivilegeBrief(po);
            idCache.put(priv.getId(), priv);
            keyCache.put(key, priv);
        }
    }

    /**
     * 以id获得缓存的Privilege对象
     * @param id
     * @return PrivilegeBrief
     */
    public PrivilegeBrief getPrivilegeBriefById(Long id){
        return this.idCache.get(id);
    }

    /**
     * 以url和RequestType获得缓存的Privilege对象
     * @param url: 访问链接
     * @param requestType: 访问类型
     * @return PrivilegeBrief
     */
    public PrivilegeBrief getPrivilegeBriefByKey(String url, Privilege.RequestType requestType){
        StringBuffer key = new StringBuffer(url);
        key.append("-");
        key.append(requestType.getCode());
        return this.keyCache.get(key.toString());
    }

}
