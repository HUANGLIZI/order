package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.bo.Orders;
import lombok.Data;

/**
 * 管理员状态VO
 * @author LiangJi3229
 * @date 2020/11/10 18:41
 */
@Data
public class StateVo {
    private Long Code;

    private String name;
    public StateVo(Orders.State state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }
}
