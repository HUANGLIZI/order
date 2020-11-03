package cn.edu.xmu.privilege.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

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
    private Integer requestType;
}
