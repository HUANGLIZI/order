package cn.edu.xmu.privilege.model.vo;

import cn.edu.xmu.privilege.model.bo.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色返回简单视图
 * 
 * @author Weice Wang
 * @date Created in 2020/11/4 11:48
 **/
@Data
@ApiModel(description = "角色视图对象")
public class RoleSimpleRetVo {
    @ApiModelProperty(value = "角色id")
    private Long id;

    @ApiModelProperty(value = "角色名称")
    private String name;

    /**
     * 用Role对象建立Vo对象
     * @param role role
     * @return RoleRetVo
     */
    public RoleSimpleRetVo(Role role) {
        this.id = role.getId();
        this.name = role.getName();
    }
}
