package cn.edu.xmu.privilege.model.vo;

import cn.edu.xmu.privilege.model.bo.UserRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 用户角色返回vo
 * @author Xianwei Wang
 **/
@Data
@ApiModel
public class UserRoleRetVo {

    @ApiModelProperty(name = "用户id", value = "userid")
    private Long id;

    private UserNameVo user;

    private RoleRetVo role;

    private UserNameVo creator;

    @ApiModelProperty(name = "创建时间", value = "gmtCreate")
    private String gmtCreate;

    public UserRoleRetVo(){

    }
}
