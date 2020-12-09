package cn.edu.xmu.order.model.vo;

import lombok.Data;

/**
 * @author Caixin
 * @date 2020-12-08 23:43
 */
@Data
public class ShopRetVo {

    private Long id;

    private String name;

    private Byte state;

    private String gmtCreate;

    private String gmtModified;
}
