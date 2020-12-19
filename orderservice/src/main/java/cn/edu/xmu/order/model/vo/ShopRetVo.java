package cn.edu.xmu.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Caixin
 * @date 2020-12-08 23:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopRetVo {

    private Long id;

    private String name;

    private Byte state;

    private String gmtCreate;

    private String gmtModified;
}
