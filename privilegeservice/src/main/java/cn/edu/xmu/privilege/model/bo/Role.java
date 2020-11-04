package cn.edu.xmu.privilege.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.privilege.model.po.RolePo;
import cn.edu.xmu.privilege.model.vo.RoleRetVo;
import cn.edu.xmu.privilege.model.vo.RoleVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色
 * @author Ming Qiu
 * @date Created in 2020/11/3 11:48
 **/
@Data
public class Role implements VoObject, Serializable {
    private Long id;
    private String name;
    private Long creatorId;
    private String describe;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Role() {
    }

    /**
     * 构造函数
     *
     * @param po 用PO构造
     */
    public Role(RolePo po) {
        this.id = po.getId();
        this.name = po.getName();
        this.creatorId = po.getCreatorId();
        this.describe = po.getDescr();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }

    @Override
    public Object createVo() {
        return new RoleRetVo(this);
    }

    /**
     * 用vo对象创建更新po对象
     * @param vo vo对象
     * @return po对象
     */
    public RolePo createUpdatePo(RoleVo vo){
        RolePo po = new RolePo();
        po.setId(this.getId());
        po.setName(vo.getName());
        po.setCreatorId(null);
        po.setDescr(vo.getDescr());
        po.setGmtCreate(null);
        po.setGmtModified(LocalDateTime.now());
        return po;
    }

    public RolePo gotRolePo(){
        RolePo po = new RolePo();
        po.setId(this.getId());
        po.setName(this.getName());
        po.setCreatorId(this.getCreatorId());
        po.setDescr(this.getDescribe());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        return po;
    }
}
