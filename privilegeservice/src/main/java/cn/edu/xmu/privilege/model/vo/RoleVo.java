package cn.edu.xmu.privilege.model.vo;

import cn.edu.xmu.privilege.model.bo.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "角色视图对象")
public class RoleVo {
    @NotBlank(message = "角色名不能为空")
    @ApiModelProperty(value = "角色名")
    private String name;

    @ApiModelProperty(value = "角色名")
    private String desc;

    public Role createRole(){
        Role role = new Role();
        role.setDescribe(this.desc);
        role.setName(this.name);
        return role;
    }
}
