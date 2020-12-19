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
public class CustomerRetVo {

    private Long id;

    private String userName;

    private String name;
}
