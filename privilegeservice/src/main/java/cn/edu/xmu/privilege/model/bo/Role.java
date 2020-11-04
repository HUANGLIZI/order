package cn.edu.xmu.privilege.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.privilege.model.po.RolePo;
import cn.edu.xmu.privilege.model.vo.RoleRetVo;
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
    /**
     * 代理对象
     */
    private RolePo rolePo;

    /**
     * 构造函数
     */
    public Role() {
        this.rolePo = new RolePo();
    }

    /**
     * 构造函数
     */
    public Role(RolePo rolePo) {
        this.rolePo = rolePo;
    }

    /**
     * 由Goods对象创建Vo对象
     */
    @Override
    public RoleRetVo createVo(){
        return new RoleRetVo(this);
    }

    /**
     * 获得内部的代理对象
     * @return GoodsPo对象
     */
    public RolePo gotRolePo(){
        return this.rolePo;
    }

    public Long getId() {
        return this.rolePo.getId();
    }

    public void setId(Long id) {
        this.rolePo.setId(id);
    }

    public String getName() {
        return this.rolePo.getName();
    }

    public void setName(String name) {
        this.rolePo.setName(name == null ? null : name.trim());
    }

    public String getDescribe() {
        return this.rolePo.getDescribe();
    }

    public void setDescribe(String describe) {
        this.rolePo.setDescribe(describe == null ? null : describe.trim());
    }

    public Long getCreatorId() {
        return this.rolePo.getCreatorId();
    }

    public void setCreatorId(Long creatorId) {
        this.rolePo.setCreatorId(creatorId);
    }

    public LocalDateTime getGmtCreate() {
        return this.rolePo.getGmtCreate();
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.rolePo.setGmtCreate(gmtCreate);
    }

    public LocalDateTime getGmtModified() {
        return this.rolePo.getGmtModified();
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.rolePo.setGmtModified(gmtModified);
    }
}
