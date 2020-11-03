package cn.edu.xmu.privilege.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @auther mingqiu
 * @date 2020/6/27 下午7:54
 */
@ApiModel
@Data
public class LoginVo {
    @ApiModelProperty(name = "用户名", value = "testuser")
    private String userName;
    @ApiModelProperty(name = "密码", value = "123456r")
    private String password;
}
