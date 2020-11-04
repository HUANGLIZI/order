package cn.edu.xmu.privilege.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.privilege.model.po.RolePo;
import cn.edu.xmu.privilege.model.po.UserPo;
import cn.edu.xmu.privilege.model.po.UserRolePo;
import cn.edu.xmu.privilege.model.vo.UserRoleRetVo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRole implements VoObject {
    private Long id;
    private User user;
    private Role role;
    private User creator;

    private LocalDateTime gmtCreate;

    public UserRole(UserRolePo userRolePo, User user, Role role, User creator){
        this.id = userRolePo.getId();
        this.user = user;
        this.role = role;
        this.creator = creator;
        this.gmtCreate = userRolePo.getGmtCreate();
    }

    @Override
    public Object createVo() {
        return new UserRoleRetVo(this);
    }
}
