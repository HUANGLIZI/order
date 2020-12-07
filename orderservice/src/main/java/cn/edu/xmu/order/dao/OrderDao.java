package cn.edu.xmu.order.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.order.mapper.OrdersPoMapper;
import cn.edu.xmu.order.model.bo.Orders;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderItemPoExample;
import cn.edu.xmu.order.model.po.OrdersPo;
import cn.edu.xmu.order.model.po.OrdersPoExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderDao {
    @Autowired
    OrdersPoMapper ordersPoMapper;
    @Autowired
    OrderItemPoMapper orderItemPoMapper;

    private static final Logger logger = LoggerFactory.getLogger(OrderDao.class);
    /**
     * ID获取订单信息
     * @author Li Zihan 24320182203227
     * @param id
     * @return 用户
     */
    public Orders findOrderById(Long id) {
        logger.debug("findUserById: Id =" + id);
        OrdersPo ordersPo = ordersPoMapper.selectByPrimaryKey(id);
        Orders orders=new Orders(ordersPo);
        if (ordersPo == null) {
            logger.error("getOrder: 订单数据库不存在该订单 orderid=" + id);
        }
        return orders;
    }
    /**
     * ID获取订单明细信息
     * @author Li Zihan 24320182203227
     * @param orderId
     * @return 订单明细列表
     */
    public List<OrderItemPo> findOrderItemById(Long orderId) {
        logger.debug("findOrderItemByOrderId: Id =" + orderId);
        OrderItemPoExample orderItemPoExample=new OrderItemPoExample();
        OrderItemPoExample.Criteria criteria=orderItemPoExample.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<OrderItemPo> orderItemPos=orderItemPoMapper.selectByExample(orderItemPoExample);
        if (orderItemPos == null) {
            logger.error("getOrder: 订单明细数据库不存在该订单 orderid=" + orderId);
        }
        return orderItemPos;
    }
    /**
     * 通过id转换订单类型
     * @author Li Zihan 24320182203227
     * @param orderId
     * @return 订单明细列表
     */
    public int transOrder(Long orderId) {
        logger.debug("transOrderByOrderId: Id =" + orderId);
        OrdersPo ordersPo = ordersPoMapper.selectByPrimaryKey(orderId);
        Byte type=0;
        ordersPo.setOrderType(type);
        int ret=ordersPoMapper.updateByPrimaryKeySelective(ordersPo);
        return ret;
    }

    /**
     * 修改某个订单
     *
     * @author 24320182203196 洪晓杰
     */
    public ReturnObject<Orders> updateOrder(Orders orders) {

        OrdersPo ordersPo = orders.gotOrdersPo();
//        OrdersPo ordersPo1=ordersPoMapper.selectByPrimaryKey(orders.getId());
//        if(ordersPo1!=null){//对应的id的记录不为空
//            ordersPo.setGmtCreate(ordersPo1.getGmtCreate());
//        }
        ReturnObject<Orders> retObj = null;
        OrdersPoExample ordersPoExample = new OrdersPoExample();
        OrdersPoExample.Criteria criteria = ordersPoExample.createCriteria();
        criteria.andIdEqualTo(orders.getId());
        //校验的时候使用？？？？？？？？？？？？
        //criteria.andDepartIdEqualTo(orders.getDepartId());
        try{
            int ret = ordersPoMapper.updateByExampleSelective(ordersPo, ordersPoExample);
            if (ret == 0) {
                //修改失败
                logger.debug("updateRole: update orders fail : " + ordersPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("角色id不存在：" + ordersPo.getId()));
            } else {
                //修改成功
                logger.debug("updateRole: update orders = " + ordersPo.toString());
                retObj = new ReturnObject<>();
            }
        }
        catch (DataAccessException e) {
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }


}
