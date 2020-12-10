package cn.edu.xmu.order.model.vo;


import cn.edu.xmu.order.model.bo.Orders;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(description = "订单详情视图对象1")
@Data
public class OrderSimpleVo {

    private String consignee;
    private Long regionId;
    private String address;
    private String mobile;
    /**
     * 构造函数
     *
     * @author 24320182203196 洪晓杰
     * @return Orders
     */
    public Orders createOrders() {
        Orders orders = new Orders();
        orders.setConsignee(this.consignee);
        orders.setRegionId(this.regionId);
        orders.setAddress(this.address);
        orders.setMobile(this.mobile);
        return orders;
    }

}
