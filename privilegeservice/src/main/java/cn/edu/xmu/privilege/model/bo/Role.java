package cn.edu.xmu.privilege.model.bo;

import cn.edu.xmu.privilege.model.po.RolePo;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色
 * @author Ming Qiu
 * @date Created in 2020/11/3 11:48
 **/
@Data
public class Role {

    private Long id;
    private String name;
    private Long creatorId;
    private String describe;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Role()
    {

    }
    public Role(RolePo rolePo){
        this.id = rolePo.getId();
        this.name = rolePo.getName();
        this.creatorId = rolePo.getCreatorId();
        this.describe = rolePo.getRoleDescribe();
        this.gmtCreate = rolePo.getGmtCreate();
        this.gmtModified = rolePo.getGmtModified();

    }
}
