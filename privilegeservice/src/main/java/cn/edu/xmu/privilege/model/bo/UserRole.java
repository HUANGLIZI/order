package cn.edu.xmu.privilege.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.SHA256;
import cn.edu.xmu.ooad.util.StringUtil;
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

    private String signature;

    private String cacuSignature;

    public UserRole(UserRolePo userRolePo, User user, Role role, User creator){
        this.id = userRolePo.getId();
        this.user = user;
        this.role = role;
        this.creator = creator;
        this.gmtCreate = userRolePo.getGmtCreate();
        this.signature = userRolePo.getSignature();

        StringBuilder signature = StringUtil.concatString("-",
                userRolePo.getUserId().toString(), userRolePo.getRoleId().toString(), userRolePo.getCreatorId().toString());
        this.cacuSignature = SHA256.getSHA256(signature.toString());
    }

    /**
     * 对象未篡改
     * @return
     */
    public Boolean authetic() {
        return this.cacuSignature.equals(this.signature);
    }

    @Override
    public Object createVo() {
        return new UserRoleRetVo(this);
    }
}
