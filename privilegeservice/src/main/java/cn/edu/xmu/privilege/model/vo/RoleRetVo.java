package cn.edu.xmu.privilege.model.vo;

import cn.edu.xmu.privilege.model.bo.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "角色视图对象")
public class RoleRetVo {
    @ApiModelProperty(value = "角色id")
    private Long id;

    @ApiModelProperty(value = "角色名称")
    private String name;

    @ApiModelProperty(value = "角色描述")
    private String desc;

    @ApiModelProperty(value = "创建者")
    private Long createdBy;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    /**
     * 用Role对象建立Vo对象
     * @param role role
     * @return RoleRetVo
     */
    public RoleRetVo(Role role) {
        this.id = role.getId();
        this.name = role.getName();
        this.desc = role.getDescribe();
        this.createdBy = role.getCreatorId();
        this.gmtCreate = role.getGmtCreate();
        this.gmtModified = role.getGmtModified();
    }
}
