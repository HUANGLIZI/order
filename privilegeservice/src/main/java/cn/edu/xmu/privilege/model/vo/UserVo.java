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
 **/
@Data
@ApiModel(description = "管理员用户信息视图对象")
public class UserVo {

    private String name;

    private String avatar;

    private String mobile;

    private String email;

    /**
     * 由 Vo 对象创建新的 User Po 对象
     * @return User Po 对象
     */
    public UserPo createUserPo(Long id) {
        UserPo po = new UserPo();
        po.setId(id);
        po.setName(this.name == null ? null : AES.encrypt(this.name, User.AESPASS));
        po.setAvatar(this.avatar);
        po.setMobile(this.mobile == null ? null : AES.encrypt(this.mobile, User.AESPASS));
        po.setEmail(this.email == null ? null : AES.encrypt(this.email, User.AESPASS));

        po.setGmtCreate(null);
        po.setGmtModified(LocalDateTime.now());

        return po;
    }
}
