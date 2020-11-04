package cn.edu.xmu.privilege.model.vo;

import cn.edu.xmu.privilege.model.bo.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "商品对象")
public class RoleRetVo {
    @ApiModelProperty(value = "角色名")
    private Long id;

    @ApiModelProperty(value = "角色名")
    private String name;

    @ApiModelProperty(value = "角色名")
    private String desc;

    @ApiModelProperty(value = "角色名")
    private Long createdBy;

    @ApiModelProperty(value = "角色名")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "角色名")
    private LocalDateTime gmtModified;
    /**
     * 构造函数，由Goods对象创建Vo
     * @param role role
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
