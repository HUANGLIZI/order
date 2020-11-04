package cn.edu.xmu.privilege.model.vo;

import cn.edu.xmu.privilege.model.bo.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户id与用户名
 * @author Xianwei Wang
 **/
@Data
@ApiModel
public class UserNameVo{
    @ApiModelProperty(name = "用户id", value = "id")
    private Long id;

    @ApiModelProperty(name = "用户名", value = "userName")
    private String userName;

    public UserNameVo(User user) {
        if (user != null) {
            this.userName = user.getUserName();
            this.id = user.getId();
        }
    }
}