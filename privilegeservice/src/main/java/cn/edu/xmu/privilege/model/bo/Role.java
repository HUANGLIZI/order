package cn.edu.xmu.privilege.model.bo;

import cn.edu.xmu.privilege.model.po.RolePo;
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

    /**
     * 构造函数
     */
    public Role()
    {

    }

    /**
     * 构造函数
     * @param rolePo po对象
     */
    public Role(RolePo rolePo) {
        this.id = rolePo.getId();
        this.name = rolePo.getName();
        this.creatorId = rolePo.getCreatorId();
        this.describe = rolePo.getDescr();
        this.gmtCreate = rolePo.getGmtCreate();
        this.gmtModified = rolePo.getGmtModified();


    }

    /**
     * 生成RoleRetVo对象作为返回前端
     * @return Object
     */
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

    /**
     * 用bo对象创建更新po对象
     * @return RolePo
     */
    public RolePo gotRolePo() {
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
