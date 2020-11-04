package cn.edu.xmu.privilege.model.vo;

import cn.edu.xmu.privilege.model.bo.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色返回对象
 * @author Xianwei Wang
 **/
@Data
@ApiModel
public class RoleRetVo {

    @ApiModelProperty(name = "id")
    private Long id;

    @ApiModelProperty(name = "名称")
    private String name;

    @ApiModelProperty(name = "创建者id")
    private Long creatorId;

    @ApiModelProperty(name = "描述")
    private String desc;

    @ApiModelProperty(name = "gmtCreate")
    private String gmtCreate;

    @ApiModelProperty(name = "gmtModified")
    private String gmtModified;

    public RoleRetVo(Role role){
        this.id = role.getId();
        this.name = role.getName();
        this.creatorId = role.getCreatorId();
        this.desc = role.getDescribe();
        this.gmtCreate = role.getGmtCreate().toString();
        if (role.getGmtModified() != null)
            this.gmtModified = role.getGmtModified().toString();
    }

}
