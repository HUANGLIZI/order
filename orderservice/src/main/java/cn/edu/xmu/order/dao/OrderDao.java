package cn.edu.xmu.order.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.OrderDTO;
import cn.edu.xmu.oomall.order.model.OrderInnerDTO;
import cn.edu.xmu.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.order.mapper.OrdersPoMapper;
import cn.edu.xmu.order.model.bo.OrderItems;
import cn.edu.xmu.order.model.bo.Orders;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderItemPoExample;
import cn.edu.xmu.order.model.po.OrdersPo;
import cn.edu.xmu.order.model.po.OrdersPoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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


    public ReturnObject shopUpdateOrder(Orders orders) {
        OrdersPo ordersPo=orders.gotOrdersPo();
        //如果该店铺不拥有这个order则查不到
        if(!isOrderBelongToShop(orders.getShopId(),orders.getId())){
            logger.error(" shopUpdateOrder: 数据库不存在该支订单 orderId="+orders.getId());
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        try{
            int ret = ordersPoMapper.updateByPrimaryKeySelective(ordersPo);
            if (ret == 0) {
                //修改失败
                logger.error("shopUpdateOrder:shop update order fail : " + ordersPo.toString());
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("订单id不存在：" + ordersPo.getId()));
            } else {
                //修改成功
                logger.debug("shopUpdateOrder:shop update order : " + ordersPo.toString());
                return new ReturnObject<>();
            }
        }
        catch (DataAccessException e) {

            // 其他数据库错误
            logger.error("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    //判断order是否属于shop
    private boolean isOrderBelongToShop(Long shopId, Long orderId){
        OrdersPoExample example=new OrdersPoExample ();
        OrdersPoExample.Criteria criteria=example.createCriteria();
        criteria.andIdEqualTo(orderId);
        criteria.andShopIdEqualTo(shopId);


        List<OrdersPo> ordersPos=ordersPoMapper.selectByExample(example);
        return !ordersPos.isEmpty();
    }


    public ReturnObject shopDeliverOrder(Orders orders) {

        OrdersPoExample example=new OrdersPoExample ();
        OrdersPoExample.Criteria criteria=example.createCriteria();
        criteria.andIdEqualTo(orders.getId());
        criteria.andShopIdEqualTo(orders.getShopId());

        List<OrdersPo> ordersPos=ordersPoMapper.selectByExample(example);


        //如果该用户不拥有这个order则查不到
        if(ordersPos.isEmpty()){
            logger.error(" shopDeliverOrder: 数据库不存在该支订单 orderId="+orders.getId());
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        //按id搜索应该只有一个po对象
        OrdersPo ordersPo=ordersPos.get(0);

        //订单未处于已支付状态，则不允许改变
        if(ordersPo.getState()!=(byte)10&&ordersPo.getState()!=(byte)11&&ordersPo.getState()!=(byte)12){
            //修改失败
            logger.error("shopDeliverOrder:Error Order State : " + ordersPo.toString());
            return new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW, String.format("订单状态无法转换为发货中：state=" + ordersPo.getState()));
        }

        //改为发货中状态
        ordersPo.setState((byte) 16);
        //设置运输sn
        ordersPo.setShipmentSn(orders.getShipmentSn());

        try{
            int ret = ordersPoMapper.updateByPrimaryKeySelective(ordersPo);
            if (ret == 0) {
                //修改失败
                logger.error("shopDeliverOrder:shop Deliver Order fail : " + ordersPo.toString());
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("订单id不存在：" + ordersPo.getId()));
            } else {
                //修改成功
                logger.debug("shopDeliverOrder:shop Deliver Order : " + ordersPo.toString());
                return new ReturnObject<>();
            }
        }
        catch (DataAccessException e) {

            // 其他数据库错误
            logger.error("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-06 21:14
     */
    public ReturnObject<OrderInnerDTO> getUserIdbyOrderId(Long orderId)
    {
        OrdersPo ordersPo = ordersPoMapper.selectByPrimaryKey(orderId);

//        ordersPoMapper.selectByExample()
        OrderInnerDTO orderInnerDTO;
        if (ordersPo != null)
        {
            orderInnerDTO = new OrderInnerDTO();
            orderInnerDTO.setCustomerId(ordersPo.getCustomerId());
        }
        else
            orderInnerDTO = new OrderInnerDTO();
        return new ReturnObject<>(orderInnerDTO);
    }

    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-07 13:17
     */
    public ReturnObject<OrderInnerDTO> getShopIdbyOrderId(Long orderId)
    {
        OrdersPo ordersPo = ordersPoMapper.selectByPrimaryKey(orderId);
        OrderInnerDTO orderInnerDTO;
        if (ordersPo != null)
        {
            orderInnerDTO = new OrderInnerDTO();
            orderInnerDTO.setShopId(ordersPo.getShopId());
        }
        else
            orderInnerDTO = new OrderInnerDTO();
        return new ReturnObject<>(orderInnerDTO);
    }
    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-06 21:13
     */
    public ReturnObject<PageInfo<VoObject>> getOrdersByUserId(Long userId, Integer pageNum, Integer pageSize,
                                                              String orderSn, Byte state,
                                                              String beginTime, String endTime) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        OrdersPoExample ordersPoExample = new OrdersPoExample();
        OrdersPoExample.Criteria criteria = ordersPoExample.createCriteria();
        if (userId == null)
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        criteria.andCustomerIdEqualTo(userId);
        // 被逻辑删除的订单不能被返回
        Byte beDeleted = 0;
        criteria.andBeDeletedEqualTo(beDeleted);
        if (orderSn != null)
            criteria.andOrderSnEqualTo(orderSn);
        if (state != null)
            criteria.andStateEqualTo(state);
        if (beginTime != null)
            criteria.andGmtCreateGreaterThanOrEqualTo(LocalDateTime.parse(beginTime, df));
        if (endTime != null)
            criteria.andGmtCreateLessThanOrEqualTo(LocalDateTime.parse(endTime, df));
        //分页查询
        PageHelper.startPage(pageNum, pageSize);
        logger.debug("page = " + pageNum + "pageSize = " + pageSize);
        List<OrdersPo> ordersPos = null;
        try {
            //不加限定条件查询所有
            ordersPos = ordersPoMapper.selectByExample(ordersPoExample);
            List<VoObject> ret = new ArrayList<>(ordersPos.size());
            for (OrdersPo po : ordersPos) {
                Orders order = new Orders(po);
                ret.add(order);
            }
            PageInfo<VoObject> orderPage = PageInfo.of(ret);
            return new ReturnObject<>(orderPage);
        } catch (DataAccessException e) {
            logger.error("selectAllRole: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    // 通过skuId查shopId
    public ReturnObject<Orders> createOrders(Orders orders, List<OrderItems> orderItemsList)
    {
        OrdersPo ordersPo = orders.gotOrdersPo();
        ReturnObject<Orders> retObj = null;
        int ret = ordersPoMapper.insertSelective(ordersPo);
        if (ret == 0) {
            //插入失败
            logger.debug("insertRefund: insert refund fail " + ordersPo.toString());
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + ordersPo.getOrderSn()));
        } else {
            //插入成功
            logger.debug("insertRefund: insert refund = " + ordersPo.toString());
            Long insertOrderId = ordersPo.getId();
            orders.setId(insertOrderId);
            for (OrderItems bo: orderItemsList)
            {
                bo.setOrderId(insertOrderId);
                OrderItemPo orderItemPo = bo.gotOrderItemPo();
                int orderItemRet = orderItemPoMapper.insertSelective(orderItemPo);
                if (orderItemRet == 0)
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + orderItemPo.getName()));
            }
            orders.setOrderItemsList(orderItemsList);
            retObj = new ReturnObject<>(orders);
        }
        return retObj;
    }

    public ReturnObject<OrderDTO> getOrderItemsForOther(Long userId, Long orderItemId)
    {
        OrderItemPo orderItemPo = orderItemPoMapper.selectByPrimaryKey(orderItemId);
        OrdersPo ordersPo = ordersPoMapper.selectByPrimaryKey(orderItemPo.getOrderId());
        if (orderItemPo == null || ordersPo == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        if (!ordersPo.getCustomerId().equals(userId))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setShopId(ordersPo.getShopId());
        orderDTO.setOrderId(orderItemPo.getOrderId());
        orderDTO.setOrderSn(ordersPo.getOrderSn());
        orderDTO.setPrice(orderItemPo.getPrice());
        orderDTO.setSkuId(orderItemPo.getGoodsSkuId());
        orderDTO.setSkuName(orderItemPo.getName());

        return new ReturnObject<>(orderDTO);
    }

    public ReturnObject<List<Long>> getOrderItemsIdForOther(Long userId, Long skuId)
    {
        OrdersPoExample ordersPoExample = new OrdersPoExample();
        OrdersPoExample.Criteria criteria = ordersPoExample.createCriteria();
        criteria.andCustomerIdEqualTo(userId);
        List<OrdersPo> ordersPos = ordersPoMapper.selectByExample(ordersPoExample);
        OrderItemPoExample orderItemPoExample = new OrderItemPoExample();
        List<Long> orderItemsIdList = new ArrayList<Long>();
        for (OrdersPo ordersPo: ordersPos)
        {
            OrderItemPoExample.Criteria orderItemCriteria = orderItemPoExample.createCriteria();
            orderItemCriteria.andGoodsSkuIdEqualTo(skuId);
            orderItemCriteria.andOrderIdEqualTo(ordersPo.getId());
            List<OrderItemPo> orderItemPos = orderItemPoMapper.selectByExample(orderItemPoExample);
            for (OrderItemPo orderItemPo: orderItemPos)
                orderItemsIdList.add(orderItemPo.getId());
        }

        return new ReturnObject<>(orderItemsIdList);
    }
}
