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
import cn.edu.xmu.order.model.vo.OrderItemsCreateVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public ReturnObject<Orders> findOrderById(Long id) {
        logger.debug("findUserById: Id =" + id);
        OrdersPo ordersPo = ordersPoMapper.selectByPrimaryKey(id);
        if (ordersPo == null) {
            logger.error("getOrder: 订单数据库不存在该订单 orderid=" + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Orders orders=new Orders(ordersPo);
        return new ReturnObject<>(orders);
    }
    /**
     * ID获取订单明细信息
     * @author Li Zihan 24320182203227
     * @param orderId
     * @return 订单明细列表
     */
    public ReturnObject<List<OrderItemPo>> findOrderItemById(Long orderId) {
        logger.debug("findOrderItemByOrderId: Id =" + orderId);
        OrdersPo ordersPo = ordersPoMapper.selectByPrimaryKey(orderId);
        if (ordersPo == null)
        {
            logger.error("getOrder: 数据库不存在该订单 orderid=" + orderId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if (ordersPo.getBeDeleted().equals((byte)1))
        {
            logger.error("getOrder: 数据库不存在该订单 orderid=" + orderId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        OrderItemPoExample orderItemPoExample=new OrderItemPoExample();
        OrderItemPoExample.Criteria criteria=orderItemPoExample.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<OrderItemPo> orderItemPos=orderItemPoMapper.selectByExample(orderItemPoExample);
        if (orderItemPos == null) {
            logger.error("getOrder: 订单明细数据库不存在该订单 orderid=" + orderId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject<>(orderItemPos);
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
        ordersPo.setSubstate(Orders.State.HAS_PAID.getCode().byteValue());
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
        ReturnObject returnObject=isOrderBelongToShop(orders.getShopId(),orders.getId());
        if(returnObject.getCode()!=ResponseCode.OK){
            return returnObject;
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
    public ReturnObject isOrderBelongToShop(Long shopId, Long orderId){
        OrdersPoExample example=new OrdersPoExample ();
        OrdersPoExample.Criteria criteria=example.createCriteria();
        criteria.andIdEqualTo(orderId);
        List<OrdersPo> ordersPos=ordersPoMapper.selectByExample(example);
        if(ordersPos.size()==0){
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        //根据id查只能查到一个
        OrdersPo po = ordersPos.get(0);
        if(!(po.getShopId().equals(shopId))){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        if(po.getShopId().toString().equals("null")){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }

        return new ReturnObject(po);
    }


    public ReturnObject shopDeliverOrder(Orders orders) {

        //如果该店铺不拥有这个order则查不到
        ReturnObject returnObject=isOrderBelongToShop(orders.getShopId(),orders.getId());
        if(returnObject.getCode()!=ResponseCode.OK){
            return returnObject;
        }
        OrdersPo ordersPo=(OrdersPo) returnObject.getData();


        if(ordersPo.getSubstate()==null){
            return new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
        }
        //订单未处于已支付状态，则不允许改变
        if(!ordersPo.getSubstate().equals((byte)21)){
            //修改失败
            System.out.println("hhh");
            logger.error("shopDeliverOrder:Error Order State : " + ordersPo.toString());
            return new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
        }

        //改为发货中状态
        ordersPo.setSubstate((byte) 24);
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

        if (ordersPo == null)
        {
            logger.info("not found order :" + orderId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
//        ordersPoMapper.selectByExample()
        OrderInnerDTO orderInnerDTO = new OrderInnerDTO();
        orderInnerDTO.setCustomerId(ordersPo.getCustomerId());
        return new ReturnObject<>(orderInnerDTO);
    }

    /**
     * @param
     * @return
     * @author Li Zihan
     * @date 2020-12-19 5:17
     */
    public ReturnObject<OrderInnerDTO> getOrderInfoByOrderId(Long orderId)
    {
        OrdersPo ordersPo = ordersPoMapper.selectByPrimaryKey(orderId);
        if (ordersPo == null)
        {
            logger.info("not found order :" + orderId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        OrderInnerDTO orderInnerDTO = new OrderInnerDTO();
        orderInnerDTO.setCustomerId(ordersPo.getCustomerId());
        orderInnerDTO.setOrderId(ordersPo.getId());
        orderInnerDTO.setState(ordersPo.getState());
        orderInnerDTO.setSubstate(ordersPo.getSubstate());
        orderInnerDTO.setShopId(ordersPo.getShopId());
        orderInnerDTO.setPrice(ordersPo.getOriginPrice() + ordersPo.getFreightPrice());
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
        if (ordersPo == null)
        {
            logger.info("not found order :" + orderId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        OrderInnerDTO orderInnerDTO = new OrderInnerDTO();
        orderInnerDTO.setShopId(ordersPo.getShopId());
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
        {
            logger.info("userId is " + userId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        criteria.andCustomerIdEqualTo(userId);
        criteria.andCustomerIdEqualTo(userId);
        // 被逻辑删除的订单不能被返回
        Byte beDeleted = 0;
        criteria.andBeDeletedEqualTo(beDeleted);
        if (orderSn != null)
            criteria.andOrderSnEqualTo(orderSn);
        if (state != null)
            criteria.andStateEqualTo(state);
        if (beginTime != null)
        {
            try {
                if (beginTime.contains("T"))
                    beginTime = beginTime.replace("T"," ");
                LocalDateTime.parse(beginTime, df);
            }
            catch (Exception e)
            {
                logger.info("时间字段不合法 " + beginTime);
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
            }
            criteria.andGmtCreateGreaterThanOrEqualTo(LocalDateTime.parse(beginTime, df));
        }
        if (endTime != null)
        {
            try {
                if (endTime.contains("T"))
                    endTime = endTime.replace("T"," ");
                LocalDateTime.parse(endTime, df);
            }
            catch (Exception e)
            {
                logger.info("时间字段不合法 " + endTime);
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
            }
            criteria.andGmtCreateLessThanOrEqualTo(LocalDateTime.parse(endTime, df));
        }
        if (beginTime != null && endTime != null)
        {
            if (LocalDateTime.parse(beginTime, df).isAfter(LocalDateTime.parse(endTime, df)))
            {
                logger.info("开始时间大于结束时间");
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
            }
        }
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
            PageInfo<OrdersPo> ordersPoPageInfo=PageInfo.of(ordersPos);
            PageInfo<VoObject> orderPage = new PageInfo<>(ret);
            orderPage.setPageSize(ordersPoPageInfo.getPageSize());
            orderPage.setPages(ordersPoPageInfo.getPages());
            orderPage.setPageNum(ordersPoPageInfo.getPageNum());
            orderPage.setTotal(ordersPoPageInfo.getTotal());
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

    /**
     * 创建订单
     * @param orders
     * @param orderItemsList
     * @return
     * @author Cai Xinlu
     * @date 2020-12-10 10:43
     */
    public ReturnObject<Orders> createOrders(Orders orders, List<OrderItems> orderItemsList)
    {
        OrdersPo ordersPo = orders.gotOrdersPo();
        ReturnObject<Orders> retObj = null;
        int ret = ordersPoMapper.insertSelective(ordersPo);
        if (ret == 0) {
            //插入失败
            logger.debug("insertOrder: insert order fail " + ordersPo.toString());
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + ordersPo.getOrderSn()));
        } else {
            //插入成功
            logger.debug("insertOrder: insert order = " + ordersPo.toString());
            Long insertOrderId = ordersPo.getId();
            orders.setId(insertOrderId);
            for (OrderItems bo: orderItemsList)
            {
                bo.setOrderId(insertOrderId);
                OrderItemPo orderItemPo = bo.gotOrderItemPo();
                int orderItemRet = orderItemPoMapper.insertSelective(orderItemPo);
                if (orderItemRet == 0)
                {
                    logger.info("insert orderItems fail = " + orderItemPo.toString());
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + orderItemPo.getName()));
                }
                bo.setId(orderItemPo.getId());
            }
            orders.setOrderItemsList(orderItemsList);
            retObj = new ReturnObject<>(orders);
        }
        return retObj;
    }


    /**
     * 店家查询商户所有订单 (概要)
     *
     * @author 24320182203323  李明明
     * @param page 页数
     * @param pageSize 每页大小
     * @return Object 查询结果
     * @date 2020/12/19
     */
    public ReturnObject<PageInfo<VoObject>> getShopAllOrders(Long shopId, Long customerId, String orderSn, String beginTime, String endTime, Integer page, Integer pageSize)
    {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        OrdersPoExample ordersPoExample = new OrdersPoExample();
        OrdersPoExample.Criteria criteria = ordersPoExample.createCriteria();
        if (shopId == null)
        {
            logger.info("userId is " + shopId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        criteria.andShopIdEqualTo(shopId);
        // 被逻辑删除的订单不能被返回
        Byte beDeleted = 0;
        criteria.andBeDeletedEqualTo(beDeleted);
        if(customerId != null)
            criteria.andCustomerIdEqualTo(customerId);
        if(orderSn != null)
            criteria.andOrderSnEqualTo(orderSn);
        if (beginTime != null)
        {
            try {
                if (beginTime.contains("T"))
                    beginTime = beginTime.replace("T"," ");
                LocalDateTime.parse(beginTime, df);
            }
            catch (Exception e)
            {
                logger.info("时间字段不合法 " + beginTime);
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
            }
            criteria.andGmtCreateGreaterThanOrEqualTo(LocalDateTime.parse(beginTime, df));
        }
        if (endTime != null)
        {
            try {
                if (endTime.contains("T"))
                    endTime = endTime.replace("T"," ");
                LocalDateTime.parse(endTime, df);
            }
            catch (Exception e)
            {
                logger.info("时间字段不合法 " + endTime);
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
            }
            criteria.andGmtCreateLessThanOrEqualTo(LocalDateTime.parse(endTime, df));
        }
        if (beginTime != null && endTime != null)
        {
            if (LocalDateTime.parse(beginTime, df).isAfter(LocalDateTime.parse(endTime, df)))
            {
                logger.info("开始时间大于结束时间");
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
            }
        }
        //分页查询
        PageHelper.startPage(page, pageSize);
        logger.debug("page = " + page + "pageSize = " + pageSize);
        List<OrdersPo> ordersPos = null;
        try {
            ordersPos = ordersPoMapper.selectByExample(ordersPoExample);
            if (ordersPos == null)
            {
                logger.info("数据库找不到");
            }
            List<VoObject> ret = new ArrayList<>(ordersPos.size());
            for (OrdersPo po : ordersPos) {
                Orders order = new Orders(po);
                ret.add(order);
            }
            PageInfo<OrdersPo> ordersPoPageInfo = PageInfo.of(ordersPos);
            PageInfo<VoObject> orderPage = new PageInfo<>(ret);
            orderPage.setTotal(ordersPoPageInfo.getTotal());
            orderPage.setPageSize(ordersPoPageInfo.getPageSize());
            orderPage.setPageNum(ordersPoPageInfo.getPageNum());
            orderPage.setPages(ordersPoPageInfo.getPages());
            return new ReturnObject<>(orderPage);
        }
        catch (DataAccessException e){
            logger.error("selectAllRole: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * 店家查询店内订单完整信息（普通，团购，预售）
     *
     * @author 24320182203323  李明明
     * @return Object 查询结果
     * @date 2020/12/12
     */
    public ReturnObject getOrderById(Long shopId, Long id)
    {
        OrdersPo ordersPo = ordersPoMapper.selectByPrimaryKey(id);
        if(ordersPo == null)
        {
            logger.error("getOrderById: shujvkubucunzai order_id=" + id);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else if(!ordersPo.getShopId().equals(shopId))
        {
            logger.error("getOrderById: dianpuidbupipei order_id=" + id);
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("店铺id不匹配：" + shopId));
        }
        Orders orders = new Orders(ordersPo);
        return new ReturnObject<>(orders);
    }

    /**
     * 管理员取消本店铺订单
     *
     * @author 24320182203323  李明明
     * @return Object 查询结果
     * @date 2020/12/12
     */
    public ReturnObject<Orders> cancelOrderById(Long shopId, Long id)
    {
        ReturnObject<Orders> ordersReturnObject = null;
        OrdersPo ordersPo = ordersPoMapper.selectByPrimaryKey(id);
        if(ordersPo == null)
        {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("不存在对应的订单id" ));
        }
        else if(!ordersPo.getShopId().equals(shopId))
        {
            logger.debug("cancelOrderById: update Order fail " + ordersPo.toString() );
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("该订单不属于该店铺" ));
        }
        else if(ordersPo.getSubstate() != null && ordersPo.getSubstate().equals(Orders.State.HAS_DELIVERRED.getCode().byteValue()))
        {
            return new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
        }
        else if(ordersPo.getState() != null &&( ordersPo.getState().equals(Orders.State.HAS_FINISHED.getCode().byteValue()) ||ordersPo.getState().equals(Orders.State.CANCEL.getCode().byteValue())))
        {
            return new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
        }
        else
        {
            Byte type = Orders.State.CANCEL.getCode().byteValue();
            ordersPo.setState(type);
            ordersPo.setSubstate(null);
            ordersPo.setGmtModified(LocalDateTime.now());
            int ret = ordersPoMapper.updateByPrimaryKey(ordersPo);
            if(ret == 0)
            {
                logger.debug("cancelOrderById: update Order fail " + ordersPo.toString() );
                ordersReturnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + ordersPo.getId()));
            }
            else
            {
                logger.debug("cancelOrderById: update Order = " + ordersPo.toString());
                ordersReturnObject = new ReturnObject<>();
            }

        }
        return ordersReturnObject;
    }

    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-10 10:50
     */
    public ReturnObject<OrderDTO> getOrderItemsForOther(Long userId, Long orderItemId)
    {
        OrderItemPo orderItemPo = orderItemPoMapper.selectByPrimaryKey(orderItemId);
        OrdersPo ordersPo = ordersPoMapper.selectByPrimaryKey(orderItemPo.getOrderId());
//        System.out.println(orderItemPo);
        if (orderItemPo == null || ordersPo == null)
        {
            logger.info("Other" + ordersPo.toString());
            logger.info("Other" + orderItemPo.toString());
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if (!ordersPo.getCustomerId().equals(userId))
        {
            logger.info("Other " + ordersPo.toString());
            logger.info("Other " + orderItemPo.toString());
            logger.info("Other " + userId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setShopId(ordersPo.getShopId());
        orderDTO.setOrderId(orderItemPo.getOrderId());
        orderDTO.setOrderSn(ordersPo.getOrderSn());
        orderDTO.setSkuId(orderItemPo.getGoodsSkuId());
        orderDTO.setSkuName(orderItemPo.getName());

        return new ReturnObject<>(orderDTO);
    }


    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-10 10:50
     */
    public ReturnObject<List<Long>> listUserSelectOrderItemId(Long userId, List<Long> skuIds)
    {
        OrdersPoExample ordersPoExample = new OrdersPoExample();
        OrdersPoExample.Criteria criteria = ordersPoExample.createCriteria();
        criteria.andCustomerIdEqualTo(userId);
        return getOrderItemsIdForOther(skuIds, ordersPoExample);
    }

    /**
     * @auther zxj
     * @param shopId
     * @param skuIds
     * @return
     */
    public ReturnObject<List<Long>> listAdminSelectOrderItemId(Long shopId, List<Long> skuIds) {
        OrdersPoExample ordersPoExample = new OrdersPoExample();
        OrdersPoExample.Criteria criteria = ordersPoExample.createCriteria();
        criteria.andShopIdEqualTo(shopId);

        return getOrderItemsIdForOther(skuIds, ordersPoExample);
    }

    private ReturnObject<List<Long>> getOrderItemsIdForOther(List<Long> skuIds, OrdersPoExample ordersPoExample) {
        List<OrdersPo> ordersPos = ordersPoMapper.selectByExample(ordersPoExample);
        if (ordersPos == null)
        {
            logger.info("not found orderItem :" + ordersPoExample.toString());
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        OrderItemPoExample orderItemPoExample = new OrderItemPoExample();
        List<Long> orderItemsIdList = new ArrayList<Long>();
        for (OrdersPo ordersPo: ordersPos)
        {
            for (Long skuId: skuIds)
            {
                OrderItemPoExample.Criteria orderItemCriteria = orderItemPoExample.createCriteria();
                orderItemCriteria.andGoodsSkuIdEqualTo(skuId);
                orderItemCriteria.andOrderIdEqualTo(ordersPo.getId());
                List<OrderItemPo> orderItemPos = orderItemPoMapper.selectByExample(orderItemPoExample);
                for (OrderItemPo orderItemPo: orderItemPos)
                    orderItemsIdList.add(orderItemPo.getId());
            }
        }

        return new ReturnObject<>(orderItemsIdList);
    }

    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-11 10:42
     */
    public ReturnObject<OrderInnerDTO> getOrderIdbyOrderItemId(Long orderItemId)
    {
        Long orderId = orderItemPoMapper.selectByPrimaryKey(orderItemId).getOrderId();
        logger.info("orderId is: " + orderId);
        if (orderId == null)
        {
            logger.info("orderId is: " + orderId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        OrdersPo ordersPo = ordersPoMapper.selectByPrimaryKey(orderId);
        if (ordersPo == null)
        {
            logger.info("ordersPo is: " + ordersPo.toString());
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        OrderInnerDTO orderInnerDTO = new OrderInnerDTO();
        orderInnerDTO.setCustomerId(ordersPo.getCustomerId());
        orderInnerDTO.setOrderId(orderId);
        orderInnerDTO.setShopId(ordersPo.getShopId());
        logger.info("orderInnerDTO is: " + orderInnerDTO.toString());
        return new ReturnObject<>(orderInnerDTO);
    }

    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-11 10:42
     */
    public ReturnObject<OrderDTO> getOrderbyOrderItemId(Long userId,Long orderItemId)
    {
        OrderItemPo orderItemPo=orderItemPoMapper.selectByPrimaryKey(orderItemId);
        if (orderItemPo == null)
        {
            logger.info("not found orderItem :" + orderItemId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Long orderId = orderItemPo.getOrderId();
        OrdersPo ordersPo = ordersPoMapper.selectByPrimaryKey(orderId);
        if (ordersPo == null)
        {
            logger.info("not found order :" + orderId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        OrderDTO orderDTO = new OrderDTO();
        if(ordersPo.getCustomerId()==userId) {
            orderDTO.setOrderId(ordersPo.getId());
            orderDTO.setOrderSn(ordersPo.getOrderSn());
            orderDTO.setSkuId(orderItemPo.getGoodsSkuId());
            orderDTO.setSkuName(orderItemPo.getName());
            orderDTO.setShopId(ordersPo.getShopId());
//            orderDTO.setPrice(orderItemPo.getPrice());
        }
        return new ReturnObject<>(orderDTO);
    }

    /*
     * @author Li Zihan
     * @date 2020-12-10 10:50
     */
    public ReturnObject<OrderItemPo> getOrderItems(Long userId, Long orderItemId)
    {
        OrderItemPo orderItemPo = orderItemPoMapper.selectByPrimaryKey(orderItemId);
        if (orderItemPo == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        OrdersPo ordersPo = ordersPoMapper.selectByPrimaryKey(orderItemPo.getOrderId());
        if (ordersPo == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        if (!ordersPo.getCustomerId().equals(userId))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        return new ReturnObject<>(orderItemPo);
    }

    /**
     * 获取orderId通过orderItemId
     * @auther 洪晓杰
     * @return
     */
    public ReturnObject<Long> getOrderIdByOrderItemId(Long orderItemId) {
        OrderItemPo orderItemPo=orderItemPoMapper.selectByPrimaryKey(orderItemId);
        logger.info("orderItemPo is " + orderItemPo.toString());
        if(orderItemPo==null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        return new ReturnObject<>(orderItemPo.getOrderId());
    }

    public ReturnObject<List<OrderItemPo>> selectOrderItemsByOrderId(Long orderId)
    {
        OrderItemPoExample orderItemPoExample = new OrderItemPoExample();
        OrderItemPoExample.Criteria criteria = orderItemPoExample.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<OrderItemPo> orderItemPos = orderItemPoMapper.selectByExample(orderItemPoExample);
        if (orderItemPos == null)
        {
            logger.info("orderItems not found, the orderId is " + orderId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject<>(orderItemPos);
    }

    public ReturnObject<OrdersPo> getOrderByOrderId(Long orderId)
    {
        OrdersPo ordersPo = ordersPoMapper.selectByPrimaryKey(orderId);
        if (ordersPo == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        return new ReturnObject<>(ordersPo);
    }

    public ReturnObject<ResponseCode> splitOrders(List<Orders> ordersList, OrdersPo ordersPo)
    {
        int ordersRet = ordersPoMapper.updateByPrimaryKeySelective(ordersPo);
        if (ordersRet == 0)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("拆单失败"));
        for (Orders orders: ordersList)
        {
            int ret = ordersPoMapper.insertSelective(orders.gotOrdersPo());
            if (ret == 0)
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("拆单失败"));
        }

        return new ReturnObject<>(ResponseCode.OK);
    }

    public ReturnObject<OrderItemPo> getOrderItemById(Long orderItemId)
    {
        OrderItemPo orderItemPo = orderItemPoMapper.selectByPrimaryKey(orderItemId);
        if (orderItemPo == null)
        {
            logger.info("not found orderItem: " + orderItemId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject<>(orderItemPo);
    }

    /**
     *通过grouponId查询所有的对应的订单
     * @param grouponId  团购活动的id
     * @date 2020-12-14
     * @author 李明明
     */
    public ReturnObject<List<Orders>> getOrdersByGrouponId(Long grouponId)
    {
        OrdersPoExample example = new OrdersPoExample();
        OrdersPoExample.Criteria criteria = example.createCriteria();
        criteria.andGrouponIdEqualTo(grouponId);

        List<OrdersPo> ordersPos = ordersPoMapper.selectByExample(example);
        if(null == ordersPos)
        {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        List<Orders> ordersList = new ArrayList<>(ordersPos.size());
        for(OrdersPo po : ordersPos)
        {
            Orders orders = new Orders(po);
            ordersList.add(orders);
        }
        return new ReturnObject<>(ordersList);
    }

    public List<OrderItemPo> findOrderItemsByTime() {
        OrderItemPoExample orderItemPoExample = new OrderItemPoExample();
        OrderItemPoExample.Criteria criteria = orderItemPoExample.createCriteria();
        criteria.andGmtCreateBetween(LocalDateTime.now().minusDays(8),LocalDateTime.now().minusDays(7));

        List<OrderItemPo> orderItemPos=orderItemPoMapper.selectByExample(orderItemPoExample);

        return orderItemPos;
    }

    public ReturnObject updateOrderItem(OrderItemPo po) {
        int ret=orderItemPoMapper.updateByPrimaryKeySelective(po);
        if(ret==0){
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject();
    }    
    /**
     * 通过presaleId查找所有对应的订单
     * @param presaleId 预售活动Id
     * @date 2020-12-14
     * @author 李明明
     */
    public ReturnObject<List<Orders>> getOrdersByPresleId(Long presaleId)
    {
        OrdersPoExample example = new OrdersPoExample();
        OrdersPoExample.Criteria criteria = example.createCriteria();
        criteria.andPresaleIdEqualTo(presaleId);

        List<OrdersPo> ordersPos = ordersPoMapper.selectByExample(example);
        if(null == ordersPos)
        {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        List<Orders> ordersList = new ArrayList<>(ordersPos.size());
        for(OrdersPo ordersPo : ordersPos)
        {
            Orders orders = new Orders(ordersPo);
            ordersList.add(orders);
        }
        return new ReturnObject<>(ordersList);
    }

    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-19 5:14
     */
    public ReturnObject<ResponseCode> userConfirm(Orders orders)
    {
        OrdersPo ordersPo = orders.gotOrdersPo();
        int ret = ordersPoMapper.updateByPrimaryKeySelective(ordersPo);
        if (ret == 0)
        {
            logger.info("update confirm state fail!");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject<>(ResponseCode.OK);
    }
}
