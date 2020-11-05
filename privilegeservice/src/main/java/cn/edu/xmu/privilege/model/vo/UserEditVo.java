package cn.edu.xmu.privilege.model.vo;

import cn.edu.xmu.ooad.util.AES;
import cn.edu.xmu.privilege.model.bo.User;
import cn.edu.xmu.privilege.model.po.UserPo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息 Vo
 * @author Han Li
 * Created at 2020/11/4 20:30
 * Modified at 2020/11/5 10:42
 **/
@Data
@ApiModel(description = "管理员用户信息视图对象")
public class UserEditVo {

    private String name;

    private String avatar;

    private String mobile;

    private String email;

}
