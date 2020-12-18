package cn.edu.xmu.order.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.ShopDetailDTO;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
import cn.edu.xmu.oomall.order.model.OrderDTO;
import cn.edu.xmu.oomall.order.model.OrderInnerDTO;
import cn.edu.xmu.oomall.order.model.SimpleFreightModelDTO;
import cn.edu.xmu.oomall.order.service.IFreightService;
import cn.edu.xmu.oomall.order.service.IOrderService;
import cn.edu.xmu.oomall.order.service.IPaymentService;
import cn.edu.xmu.oomall.other.model.CustomerDTO;
import cn.edu.xmu.oomall.other.service.IAddressService;
import cn.edu.xmu.oomall.other.service.IAftersaleService;
import cn.edu.xmu.oomall.other.service.ICustomerService;
import cn.edu.xmu.order.dao.OrderDao;
import cn.edu.xmu.order.model.bo.OrderItems;
import cn.edu.xmu.order.model.bo.Orders;
import cn.edu.xmu.order.model.bo.Strategy;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrdersPo;
import cn.edu.xmu.order.model.vo.*;
import com.github.pagehelper.PageInfo;
import net.bytebuddy.asm.Advice;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService
public class OrderService implements IOrderService {

    @Autowired
    private OrderDao orderDao;

    @DubboReference
    private IGoodsService goodsServiceI;

    @DubboReference
    private IAftersaleService aftersaleServiceI;

    @DubboReference
    private ICustomerService customerServiceI;

    @DubboReference
    private IFreightService freightServiceI;

    @DubboReference
    private IAddressService addressServiceI;

    @DubboReference
    private IPaymentService paymentServiceI;

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

        Long userId=orders.getCustomerId();
        CustomerDTO customerDTO = customerServiceI.findCustomerByUserId(userId).getData();
        CustomerRetVo customerRetVo = new CustomerRetVo();
        customerRetVo.setId(userId);
        customerRetVo.setName(customerDTO.getName());
        customerRetVo.setUserName(customerDTO.getUserName());

        Long shopId=orders.getShopId();
        ShopDetailDTO shopDetailDTO = goodsServiceI.getShopInfoByShopId(shopId).getData();
        ShopRetVo shopRetVo = new ShopRetVo();
        shopRetVo.setId(shopDetailDTO.getShopId());
        shopRetVo.setGmtCreate(shopDetailDTO.getGmtCreate());
        shopRetVo.setGmtModified(shopDetailDTO.getGmtModified());
        shopRetVo.setName(shopDetailDTO.getName());
        shopRetVo.setState(shopDetailDTO.getState());

        for (OrderItemPo po: orderItemPos)
        {
            OrderItems orderItems = new OrderItems(po);
            orderItemsList.add(orderItems);
        }
        if(orders != null) {
            logger.debug("findOrdersById : " + returnObject);
            //OrderRetVo orderRetVo=new orderRetVo();
            returnObject = new ReturnObject(new OrderRetVo(orders));
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
    public ReturnObject<VoObject> transOrder(Long id,Long userId) {
        ReturnObject<VoObject> returnObject = null;
        Orders orders=orderDao.findOrderById(id);
        if(orders==null||orders.getOrderType()==null||orders.getBeDeleted()==1){//订单不存在
            returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else if(orders.getOrderType()==1&&orders.getCustomerId().equals(userId)&&orders.getBeDeleted()!=1) {
            if(orders.getSubstate()==22||orders.getSubstate()==23||orders.getState()==2) {
                int ret=orderDao.transOrder(id);
                if(ret == 1) {
                logger.debug("transOrdersById : " + returnObject);
                Byte type=0;
                orders.setOrderType(type);
                //OrderRetVo orderRetVo=new orderRetVo();
                returnObject = new ReturnObject(orders);
                }
                else {
                    logger.debug("findOrdersById: Not Found");
                    returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                }
            }
            else{
                returnObject=new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
            }
        }

        else if(orders.getOrderType()!=1){
            logger.debug("该订单不是团购订单，无法进行转换");
            returnObject=new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
        }
        else if(orders.getCustomerId()!=userId) {//id不对应
            returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        return returnObject;
    }


    /**
     * 买家修改本人名下订单
     *
     * @author 24320182203196 洪晓杰
     * @修改：李明明 2020-12-19
     */
    @Transactional
    public ReturnObject<VoObject> updateOrders(Orders orders, Long userId) {

        ReturnObject<VoObject> retOrder=null;
        //ReturnObject<OrderInnerDTO> returnObject=orderDao.getUserIdbyOrderId(orders.getId());
        //调用OrderDao层中的 ReturnObject<OrdersPo> getOrderByOrderId(Long orderId)
        ReturnObject<OrdersPo> returnObject = orderDao.getOrderByOrderId(orders.getId());
        if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST))
        {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        //校验前端数据的userID
        //如果ordersId与所属customerId不一致，则无法修改
        if(!userId.equals(returnObject.getData().getCustomerId())){
            //操作的资源id不是自己的对象
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        //若订单已发货，则无法修改
        if(returnObject.getData().getSubstate().equals(Orders.State.HAS_DELIVERRED.getCode().byteValue()))
        {
            return new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
        }

        ReturnObject<Orders> retObj = orderDao.updateOrder(orders);

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
                                                         String beginTimeStr, String endTimeStr)
    {
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
     * @date 2020/12/12
     */
    @Transactional
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
     * @date 2020/12/12
     */
    @Transactional
    public ReturnObject<VoObject> getOrderById(Long shopId, Long id)
    {
        Orders orders = (Orders) orderDao.getOrderById(shopId,id).getData();
        if(orderDao.getOrderById(shopId,id).getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
        {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if(orderDao.getOrderById(shopId,id).getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
        {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        List<OrderItemPo> orderItemPos=orderDao.findOrderItemById(id);

        List<OrderItems> orderItemsList = new ArrayList<OrderItems>();
        for(OrderItemPo po : orderItemPos)
        {
            OrderItems orderItems = new OrderItems(po);
            orderItemsList.add(orderItems);
        }


//        Long customerId = orders.getCustomerId();
//        CustomerDTO customerDTO = customerServiceI.findCustomerByUserId(customerId).getData();
//        CustomerRetVo customerRetVo = new CustomerRetVo();
//        customerRetVo.setId(customerId);
//        customerRetVo.setName(customerDTO.getName());
//        customerRetVo.setUserName(customerDTO.getUserName());
//
//        ShopRetVo shopRetVo = new ShopRetVo();
//        ShopDetailDTO shopDetailDTO = goodsServiceI.getShopInfoByShopId(shopId).getData();
//        shopRetVo.setId(shopId);
//        shopRetVo.setName(shopDetailDTO.getName());
//        shopRetVo.setState(shopDetailDTO.getState());
//        shopRetVo.setGmtCreate(shopDetailDTO.getGmtCreate());
//        shopRetVo.setGmtModified(shopDetailDTO.getGmtModified());

        ReturnObject<VoObject> returnObject = null;
        if(orders != null) {
            logger.debug("findOrdersById : " + returnObject);
            returnObject = new ReturnObject(new OrderCreateRetVo(orders));
        } else {
            logger.debug("findOrdersById: Not Found");
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return returnObject;
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
    public ReturnObject<OrderDTO> getUserSelectSOrderInfo(Long userId, Long orderItemId)
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
    public ReturnObject<List<Long>> listUserSelectOrderItemIdBySkuList(Long userId, List<Long> skuId)
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
    public ReturnObject<List<Long>> listAdminSelectOrderItemIdBySkuList(Long shopId, List<Long> skuId) {
        return orderDao.listAdminSelectOrderItemId(shopId, skuId);
    }

    @Override
    public ReturnObject isOrderBelongToShop(Long shopId, Long orderId) {
        return orderDao.isOrderBelongToShop(shopId, orderId);
    }

    @Override
    public  ReturnObject<Long> getAdminHandleExchange(Long userId, Long shopId, Long orderItemId, Integer quantity, Long aftersaleId){
        OrderItemPo orderItemPo=orderDao.getOrderItems(userId,orderItemId).getData();
        List<OrderItems> orderItemsList = new ArrayList();
        OrderItems orderItems=new OrderItems(orderItemPo);
        Orders orders=(Orders) orderDao.getOrderById(shopId,orderItemPo.getOrderId()).getData();
        orders.setId(null);
        orderItemPo.setId(null);
        orderItemPo.setQuantity(quantity);
        orderItemsList.add(0,orderItems);
        ReturnObject<Long> returnObject;
        returnObject=new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
        if(orders.getState()==6) {
            ReturnObject<Orders> orders1 = orderDao.createOrders(orders, orderItemsList);
            if (!orders1.getCode().equals(ResponseCode.OK))
                returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            else returnObject=new ReturnObject<>(orders1.getData().getId());
        }
        return returnObject;
    }




    @Override
    public ReturnObject<OrderInnerDTO> findOrderIdbyOrderItemId(Long orderItemId)
    {
        return orderDao.getOrderIdbyOrderItemId(orderItemId);
    }

    /**
     * Li Zihan
     * @param userId
     * @param orderItemIdList
     * @return
     */
    @Override
    public ReturnObject<Map<Long,OrderDTO>> getUserSelectOrderInfoByList(Long userId, List<Long>orderItemIdList)
    {
        Map<Long,OrderDTO> map = new HashMap<>();
        for(int i=0;i<orderItemIdList.size();i++) {
           OrderDTO orderDTO=orderDao.getOrderbyOrderItemId(userId,orderItemIdList.get(i)).getData();
           if(orderDTO!=null) {
                map.put(orderItemIdList.get(i),orderDTO);
           }
        }
        return new ReturnObject<>(map);
    }

    /**
     * 获取orderId通过orderItemId
     * @auther 洪晓杰
     * @return
     */
    @Override
    public ReturnObject<Long> getOrderIdByOrderItemId(Long orderItemId){
        logger.info("orderItemId is " + orderItemId);
        return orderDao.getOrderIdByOrderItemId(orderItemId);
    }

    /**
     * 拆单
     * @param orderId
     * @return
     * @author Cai Xinlu
     * @date 2020-12-12 14:11
     */
    @Transactional
    @Override
    public ReturnObject<ResponseCode> splitOrders(Long orderId)
    {
        List<OrderItemPo> orderItemPos = orderDao.selectOrderItemsByOrderId(orderId).getData();
        OrdersPo ordersPo = orderDao.getOrderByOrderId(orderId).getData();
        ordersPo.setState(Orders.State.HAS_PAID.getCode().byteValue());
        List<Long> shopIdList = new ArrayList<Long>();
//        List<Long> skuIdList = new ArrayList<Long>();
        for (OrderItemPo po: orderItemPos)
            shopIdList.add(goodsServiceI.getShopIdBySkuId(po.getId()).getData());
//            skuIdList.add(po.getId());
//        System.out.println(orderItemPos.size());
//        System.out.println("==========");
//        for ()
//        shopIdList.add(1L);
//        shopIdList.add(1L);
//        shopIdList.add(2L);
//        shopIdList.add(3L);
        Map<Long, Long> ordersPriceMap = new HashMap<>();
        for (int i = 0; i < orderItemPos.size(); i++)
        {
            if (ordersPriceMap.containsKey(shopIdList.get(i)))
            {
                Long p = ordersPriceMap.get(shopIdList.get(i));
                p += orderItemPos.get(i).getPrice();
                ordersPriceMap.put(shopIdList.get(i), p);
            }
            else
                ordersPriceMap.put(shopIdList.get(i), orderItemPos.get(i).getPrice());
        }
        List<Orders> ordersList = new ArrayList<Orders>();
        for(Map.Entry<Long, Long> entry : ordersPriceMap.entrySet())
        {
            Orders orders = new Orders(ordersPo);
            orders.setId(null);
            orders.setShopId(entry.getKey());
            orders.setOriginPrice(entry.getValue());
            orders.setOrderSn(Common.genSeqNum());
            orders.setPid(ordersPo.getId());
            orders.setGmtCreated(LocalDateTime.now());
            if (ordersPo.getFreightPrice() != null && entry.getValue() != null && ordersPo.getOriginPrice() != null)
                orders.setFreightPrice(ordersPo.getFreightPrice()*(entry.getValue()/ordersPo.getOriginPrice()));
            ordersList.add(orders);
        }
        return orderDao.splitOrders(ordersList, ordersPo);
    }


    /**
     * 根据orderItemId查询订单详情表和订单表信息，同时验证该orderItem是否属于该商店，shopId为0时表示管理员 无需验证
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-12 15:12
     */
    @Override
    public ReturnObject<OrderDTO> getShopSelectOrderInfo(Long shopId, Long orderItemId)
    {
        OrderDTO orderDTO = new OrderDTO();
        OrderItemPo orderItemPo = orderDao.getOrderItemById(orderItemId).getData();
        if (orderItemPo == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        ReturnObject<OrdersPo> returnObject = orderDao.getOrderByOrderId(orderItemPo.getOrderId());
        if (!returnObject.getCode().equals(ResponseCode.OK))
        {
            logger.info("getOrderById: 数据库不存在该订单 order_id=" + orderItemPo.getOrderId());
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        OrdersPo ordersPo = returnObject.getData();
        if (!ordersPo.getShopId().equals(shopId))
        {
            logger.info("getOrderById: 店铺Id不匹配 order_id=" + orderItemPo.getOrderId());
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("店铺id不匹配：" + shopId));
        }
        orderDTO.setSkuName(orderItemPo.getName());
        orderDTO.setSkuId(orderItemPo.getGoodsSkuId());
        orderDTO.setOrderSn(ordersPo.getOrderSn());
        orderDTO.setOrderId(orderItemPo.getOrderId());
        orderDTO.setShopId(ordersPo.getShopId());

        return new ReturnObject<>(orderDTO);
    }

    /**
     * 根据orderItemIdList查询订单详情表和订单表信息，同时验证该orderItem是否属于该店铺，返回orderItemId为key的Map
     * @param shopId
     * @param orderItemIdList
     * @auther zxj
     * @return
     */
    @Override
    public ReturnObject<Map<Long, OrderDTO>> getShopSelectOrderInfoByList(Long shopId, List<Long> orderItemIdList) {
        Map<Long,OrderDTO> map = new HashMap<>();
        for(Long orderItemId:orderItemIdList){
            ReturnObject<OrderDTO> returnObject = getShopSelectOrderInfo(shopId,orderItemId);
            if(returnObject.getCode()==ResponseCode.OK){
                map.put(orderItemId,returnObject.getData());
            }
        }
        return new ReturnObject<>(map);
    }

    /**
     *团购活动下架后将所有订单转换为普通订单
     * @param grouponId  团购活动的id
     * @date 2020-12-14
     * @author 李明明
     */
    @Transactional
    @Override
    public ReturnObject<Object> putGrouponOffshelves(Long grouponId)
    {
        ReturnObject<List<Orders>> returnObject1 = orderDao.getOrdersByGrouponId(grouponId);
        if(returnObject1.getCode() == ResponseCode.RESOURCE_ID_OUTSCOPE)
        {
            return new ReturnObject<>(ResponseCode.OK);//如果没有查到属于该groupId的订单，就不用进行转换处理，同样是成功的。
        }
        else
        {
            List<Orders> ordersList = returnObject1.getData();
            for(Orders orders : ordersList)
            {
                //对每个订单调用本类中 ”public ReturnObject<VoObject> transOrder(Long id)“方法
                ReturnObject<VoObject> returnObject = this.transOrder(orders.getId(),orders.getCustomerId());
                if(returnObject.getCode() != ResponseCode.OK)
                {
                    //如果其中有一个无法转换，就返回错误
                    return new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW, String.format("该订单不是团购订单，无法进行转换,OrderId:" + orders.getId()));
                }
            }
            return new ReturnObject<>(ResponseCode.OK);
        }

    }

    /**
     * 预售活动取消后，需要将所有的预售订单退款（需要将所有订单的状体置为取消）
     * @param presaleId 预售活动Id
     * @date 2020-12-14
     * @author 李明明
     */
    @Override
    public ReturnObject<Object> putPresaleOffshevles(Long presaleId)
    {
        ReturnObject<List<Orders>> returnObject1 = orderDao.getOrdersByPresleId(presaleId);

        if(returnObject1.getCode() == ResponseCode.RESOURCE_ID_OUTSCOPE)
        {
            return new ReturnObject<Object>(ResponseCode.OK); //如果没有查到属于该presaleId的订单，就不用进行转换处理，同样是成功的。
        }
        else
        {
            List<Orders> ordersList = returnObject1.getData();
            for(Orders orders : ordersList)
            {
                //对每个orders调用本类中 “public ReturnObject<VoObject> cancelOrderById(Long shopId,Long id)”
                ReturnObject<VoObject> returnObject = this.cancelOrderById(orders.getShopId(),orders.getId());
                if(returnObject.getCode() != ResponseCode.OK)
                {
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("订单更新失败 orderId：" + orders.getId()));
                }
                ReturnObject<ResponseCode> codeReturnObject = paymentServiceI.createRefundbyOrederId(orders.getShopId(),orders.getId());
                if(codeReturnObject.getCode() != ResponseCode.OK)
                {
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("退款单创建失败，orderId：" + orders.getId()));
                }
            }
            return new ReturnObject<>(ResponseCode.OK);
        }
    }

    /**
     * 1.定时器到了，我们告诉你们团购ID及里面的规则
     * 2.你们去找有多少订单参与了活动
     * 3.你们根据订单数判断是否成团及成团等级
     * 4.如果失败，问顾客退单还是转成普通订单（先弄成直接变普通订单的）
     * 5.如果成功，每个订单按成团等级找core确定优惠金额
     * 6.返还优惠
     * @param strategy 团购规则
     * @param GrouponId 团购id
     * @return
     * @author 李明明
     * @date 2020-12-14
     */
    @Transactional
    @Override
    public ReturnObject<Object> grouponEnd(String strategy, Long GrouponId)
    {
        Strategy strategy1 = JacksonUtil.toObj(strategy, Strategy.class);//不知道是不是这样用
        ReturnObject<List<Orders>> returnObject1 = orderDao.getOrdersByGrouponId(GrouponId);
        if(returnObject1.getCode() == ResponseCode.RESOURCE_ID_OUTSCOPE)
        {
            return new ReturnObject<>(ResponseCode.OK);
        }
        List<Orders> ordersList = returnObject1.getData();
        Integer orderAmount = ordersList.size();
        //System.out.println("!!!!!!"+ orderAmount + "!!!!!!!!!");
        Long refundPrice = 0L;
        List<Strategy.level> levelList = strategy1.getData();
        for(Strategy.level level : levelList)
        {
            if(orderAmount >= level.getNum() && level.getPrice() > refundPrice)
                refundPrice = level.getPrice();
        }
        //ystem.out.println("@@@@@@@@@@@"+ refundPrice + "@@@@@@@@@@@@");
        //验证退款金额是否超过支付金额,因为团购订单不允许使用优惠券,且每个订单中的订单明细表中只有一个元素,
        // 所以每个订单的originPrice就是支付的价格,如果退款金额大于等于支付金额,就返回错误.
        Long originPrice = ordersList.get(0).getOriginPrice();
        //System.out.println("###########" + originPrice + "#############");
        if(originPrice <= refundPrice)
            return new ReturnObject<>(ResponseCode.REFUND_MORE);
        if(refundPrice == 0L)//没有成团
        {
            for(Orders orders : ordersList)
            {
                //对每个订单调用本类中 ”public ReturnObject<VoObject> transOrder(Long id)“方法
                ReturnObject<VoObject> returnObject = this.transOrder(orders.getId(),orders.getCustomerId());
                if(returnObject.getCode() != ResponseCode.OK)
                {
                    //如果其中有一个无法转换，就返回错误
                    return new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW, String.format("该订单不是团购订单，无法进行转换,OrderId:" + orders.getId()));
                }
            }
            return new ReturnObject<>(ResponseCode.OK);
        }
        //下面是处理成团的情况,首先需要将支付单中的actual_amount换成减去团购优惠后的金额,然后为该支付单创建退款单
        for(Orders orders : ordersList)
        {
            //System.out.println("%%%%%%%%%%%%%%%%%%%处理成团的情况");
            ReturnObject<ResponseCode> returnObject = paymentServiceI.createRefundForGrouponByOrderId(orders.getId(),orders.getShopId(),refundPrice);
            if(returnObject.getCode() != ResponseCode.OK)
            {
                return new ReturnObject<>(returnObject.getCode(),returnObject.getErrmsg());
            }
        }
        return new ReturnObject<>(ResponseCode.OK);
    }

}
