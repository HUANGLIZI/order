package cn.edu.xmu.privilege.model.vo;

import cn.edu.xmu.privilege.model.bo.UserRole;
import lombok.Data;

@Data
public class UserRoleRetVo {
    private Long id;
    private UserNameVo user;
    private RoleRetVo role;
    private UserNameVo creator;

    private String gmtCreate;

    public UserRoleRetVo(UserRole userRole){
        this.id = userRole.getId();
        this.user = new UserNameVo(userRole.getUser());
        this.role = new RoleRetVo(userRole.getRole());
        this.creator = new UserNameVo(userRole.getCreator());

        this.gmtCreate = userRole.getGmtCreate().toString();

    }
}
