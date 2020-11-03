package cn.edu.xmu.privilege.model.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色
 * @author Ming Qiu
 * @date Created in 2020/11/3 11:48
 **/
@Data
public class Role {

    private Long id;
    private String name;
    private Long creatorId;
    private String describe;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
