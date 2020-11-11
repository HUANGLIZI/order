package cn.edu.xmu.privilege.model.vo;

import cn.edu.xmu.privilege.model.po.NewUserPo;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

/**
 * 新用户VO
 * @author LiangJi@3229
 * @date 2020/11/10 18:41
 */
@Data
public class NewUserVo {
    /*
    Minimum eight characters, at least one letter and one number:
    "^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$"
    Minimum eight characters, at least one letter, one number and one special character:
    "^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$"
    Minimum eight characters, at least one uppercase letter, one lowercase letter and one number:
    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$"
    Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character:
    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$"
    Minimum eight and maximum 10 characters, at least one uppercase letter, one lowercase letter, one number and one special character:
    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,10}$"
     */
    @Length(min=6,message = "用户名长度过短")
    private String userName;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",message = "密码格式不正确，请包含大小写字母数字及特殊符号")
    private String password;

    private String name;
    //@URL(message = "URL格式不正确")
    private String avatar;
    @Pattern(regexp="[+]?[0-9*#]+",message="手机号格式不正确")
    private String mobile;
    @Email(message = "email格式不正确")
    private String email;

    private String openId;

    private Long departId;
}
