package cn.edu.xmu.order.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.OrderDTO;
import cn.edu.xmu.oomall.order.model.OrderInnerDTO;
import cn.edu.xmu.oomall.order.service.IOrderItemService;
import cn.edu.xmu.oomall.order.service.IOrderService;
import cn.edu.xmu.order.dao.OrderDao;
import cn.edu.xmu.order.model.bo.OrderItems;
import cn.edu.xmu.order.model.bo.Orders;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrdersPo;
import cn.edu.xmu.order.model.vo.OrderCreateRetVo;
import cn.edu.xmu.order.model.vo.OrderItemsCreateVo;
import cn.edu.xmu.order.model.vo.OrderRetVo;
import cn.edu.xmu.order.model.vo.OrdersVo;
import com.github.pagehelper.PageInfo;
import io.lettuce.core.StrAlgoArgs;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@DubboService
public class OrderService<OrdersPo> implements IOrderService, IOrderItemService {

    @Autowired
    private OrderDao orderDao;

    private Logger logger = LoggerFactory.getLogger(OrderService.class);
    /**
     * ID获取订单信息
     * @author Li Zihan
     * @param id
     * @return VoObject
     */
    @Transactional
    public ReturnObject<VoObject> getOrdersByOrderId(Long id) {
        ReturnObject<VoObject> returnObject = null;
        Orders orders=orderDao.findOrderById(id);
        List<OrderItemPo> orderItemPos=orderDao.findOrderItemById(id);
        List<OrderItems> orderItemsList = new ArrayList<OrderItems>();
        for (OrderItemPo po: orderItemPos)
        {
            OrderItems orderItems = new OrderItems(po);
            orderItemsList.add(orderItems);
        }
        if(orders != null) {
            logger.debug("findOrdersById : " + returnObject);
            //OrderRetVo orderRetVo=new orderRetVo();
            returnObject = new ReturnObject(new OrderRetVo(orders,orderItemsList));
        } else {
            logger.debug("findOrdersById: Not Found");
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return returnObject;
    }

    /**
     * 转换订单类型
     * @author Li Zihan
     * @param id
     * @return VoObject
     */
    @Transactional
    public ReturnObject<VoObject> transOrder(Long id) {
        ReturnObject<VoObject> returnObject = null;
        Orders orders=orderDao.findOrderById(id);
        if(orders.getOrderType()==1) {
            int ret=orderDao.transOrder(id);
            if(ret == 1) {
                logger.debug("transOrdersById : " + returnObject);
                Byte type=0;
                orders.setOrderType(type);
                //OrderRetVo orderRetVo=new orderRetVo();
                returnObject = new ReturnObject(orders);
            } else {
                logger.debug("findOrdersById: Not Found");
                returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
        }
        else if(orders.getOrderType()!=1){
            logger.debug("该订单不是团购订单，无法进行转换");
            returnObject=new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
        }
        else if(orders==null||orders.getOrderType()==null){
            returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return returnObject;
    }


    /**
     * 买家修改本人名下订单
     *
     * @author 24320182203196 洪晓杰
     */
    @Transactional
    public ReturnObject<Object> updateOders(Orders orders) {
        ReturnObject<Orders> retObj = orderDao.updateOrder(orders);
        ReturnObject<Object> retOrder;
        if (retObj.getCode().equals(ResponseCode.OK)) {
            retOrder = new ReturnObject<>(retObj.getData());
        } else {
            retOrder = new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
        }
        return retOrder;

    }

    /**
     * 店家修改订单
     * @param orders
     * @return
     */
    public ReturnObject<Object> shopUpdateOrder(Orders orders) {
        ReturnObject returnObject = orderDao.shopUpdateOrder(orders);
        if(returnObject.getCode().equals(ResponseCode.OK)){
            return returnObject;
        }else {
            return new ReturnObject<>(returnObject.getCode(),returnObject.getErrmsg());
        }
    }

    /**
     * 店家对订单标记发货
     * @param orders
     * @return
     */
    public ReturnObject<Object> shopDeliverOrder(Orders orders) {
        ReturnObject returnObject = orderDao.shopDeliverOrder(orders);
        if(returnObject.getCode().equals(ResponseCode.OK)){
            return returnObject;
        }else {
            return new ReturnObject<>(returnObject.getCode(),returnObject.getErrmsg());
        }
    }


    /**
     * 通过订单Id查找用户Id
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-06 21:14
     */
    @Override
    public ReturnObject<OrderInnerDTO> findUserIdbyOrderId(Long orderId)
    {
        return orderDao.getUserIdbyOrderId(orderId);
    }

    @Override
    public ReturnObject<OrderInnerDTO> findShopIdbyOrderId(Long orderId) { return orderDao.getShopIdbyOrderId(orderId); }
    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-06 21:13
     */
    public ReturnObject<PageInfo<VoObject>> selectOrders(Long userId, Integer pageNum, Integer pageSize,
                                                         String orderSn, Byte state,
                                                         String beginTimeStr, String endTimeStr) {
        ReturnObject<PageInfo<VoObject>> returnObject = orderDao.getOrdersByUserId(userId, pageNum, pageSize,
                orderSn, state, beginTimeStr, endTimeStr);
        return returnObject;
    }


    /**
     * 店家查询商户所有订单 (概要)
     *
     * @author 24320182203323  李明明
     * @param page 页数
     * @param pageSize 每页大小
     * @return Object 查询结果
     */
    public ReturnObject<PageInfo<VoObject>> getShopAllOrders(Long shopId, Long customerId, String orderSn, String beginTime, String endTime, Integer page, Integer pageSize)
    {
        ReturnObject<PageInfo<VoObject>> returnObject = orderDao.getShopAllOrders(shopId,customerId, orderSn, beginTime, endTime, page, pageSize);
        return returnObject;
    }

    /**
     * 店家查询店内订单完整信息（普通，团购，预售）
     *
     * @author 24320182203323  李明明
     * @return Object 查询结果
     */
    public ReturnObject getOrderById(Long shopId, Long id)
    {
        return orderDao.getOrderById(shopId, id);
    }

    /**
     * 管理员取消本店铺订单
     *
     * @author 24320182203323  李明明
     * @return Object 查询结果
     */
    @Transactional
    public ReturnObject<VoObject> cancelOrderById(Long shopId,Long id)
    {
        ReturnObject<VoObject> returnObject;
        ReturnObject<Orders> returnObject1 = orderDao.cancelOrderById(shopId, id);
        if(returnObject1.getCode() == ResponseCode.OK)
            returnObject = new ReturnObject<>(returnObject1.getData());
        else
            returnObject = new ReturnObject<>(returnObject1.getCode(),returnObject1.getErrmsg());
        return returnObject;
    }

    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-09 23:28
     */
    @Override
    public ReturnObject<OrderDTO> getSelectOrderInfo(Long userId, Long orderItemId)
    {
        return orderDao.getOrderItemsForOther(userId, orderItemId);
    }

    /**
     * @param userId
     * @param skuId
     * @return
     * @author Cai Xinlu
     * @date 2020-12-10 10:49
     */
    @Override
    public ReturnObject<List<Long>> listUserSelectOrderItemId(Long userId, Long skuId)
    {
        return orderDao.listUserSelectOrderItemId(userId, skuId);
    }

    /**
     * @auther zxj
     * @param shopId
     * @param skuId
     * @return
     */
    @Override
    public ReturnObject<List<Long>> listAdminSelectOrderItemId(Long shopId, Long skuId) {
        return orderDao.listAdminSelectOrderItemId(shopId, skuId);
    }

    @Override
    public ReturnObject<Boolean> isOrderBelongToShop(Long shopId, Long orderId) {
        return new ReturnObject<>(orderDao.isOrderBelongToShop(shopId, orderId));
    }

    @Override
    public  ReturnObject<ResponseCode> getAdminHandleRefund(Long userId, Long shopId, Long orderItemId, Integer quantity){
        OrderItemPo orderItemPo=orderDao.getOrderItems(userId,orderItemId).getData();
        List<OrderItems> orderItemsList = new ArrayList();
        OrderItems orderItems=new OrderItems(orderItemPo);
        Orders orders=(Orders) orderDao.getOrderById(shopId,orderItemPo.getOrderId()).getData();
        orders.setId(null);
        orderItemPo.setId(null);
        orderItemPo.setQuantity(quantity);
        orderItemsList.add(0,orderItems);
        ReturnObject<ResponseCode> returnObject;
        returnObject=new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
        if(orders.getState()==6) {
            orderDao.createOrders(orders,orderItemsList);
            returnObject=new ReturnObject<>(ResponseCode.OK);
        }
        return returnObject;
    }

    @Override
    public ReturnObject<OrderInnerDTO> findOrderIdbyOrderItemId(Long orderItemId)
    {
        return orderDao.getOrderIdbyOrderItemId(orderItemId);
    }

    /**
     * 获取orderId通过orderItemId
     * @auther 洪晓杰
     * @return
     */
    @Override
    public ReturnObject<Long> getOrderIdByOrderItemId(Long orderItemId){
        return orderDao.getOrderIdByOrderItemId(orderItemId);
    }



}
