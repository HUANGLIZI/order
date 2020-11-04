package cn.edu.xmu.privilege.model.vo;

import cn.edu.xmu.privilege.model.bo.User;
import lombok.Data;

@Data
public class UserNameVo{
    private Long id;
    private String userName;

    public UserNameVo(User user) {
        if (user != null) {
            this.userName = user.getUserName();
            this.id = user.getId();
        }
    }
}