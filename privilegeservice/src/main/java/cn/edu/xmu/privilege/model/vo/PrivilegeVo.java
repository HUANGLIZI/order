package cn.edu.xmu.privilege.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

/**
 * 权限传值对象
 * @author Ming Qiu
 * @date Created in 2020/11/4 0:08
 **/
@Data
@ApiModel("权限传值对象")
public class PrivilegeVo {
    private String name;
    private String url;

    @NotBlank(message = "requestType不能为空")
    @Range(min = 0, max = 3, message = "错误的操作类型")
    private Integer requestType;
}
