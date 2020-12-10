package cn.edu.xmu.order.service;

import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.GoodsDetailDTO;
import cn.edu.xmu.oomall.goods.model.ShopDetailDTO;
import cn.edu.xmu.oomall.goods.service.GoodsService;
import cn.edu.xmu.oomall.order.model.FreightDTO;
import cn.edu.xmu.oomall.order.service.IFreightService;
import cn.edu.xmu.oomall.other.model.CustomerDTO;
import cn.edu.xmu.oomall.other.service.IAddressService;
import cn.edu.xmu.oomall.other.service.IAftersaleService;
import cn.edu.xmu.order.dao.OrderDao;
import cn.edu.xmu.order.model.bo.OrderItems;
import cn.edu.xmu.order.model.bo.Orders;
import cn.edu.xmu.order.model.vo.*;
import io.swagger.models.auth.In;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Caixin
 * @date 2020-12-08 16:06
 */
@Service
public class OrderServiceI {

    @Autowired
    private OrderDao orderDao;

    @DubboReference
    private GoodsService goodsService;

    @DubboReference
    private IAftersaleService aftersaleServiceI;

    @DubboReference
    private IFreightService freightServiceI;

    @DubboReference
    private IAddressService addressServiceI;
    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-08 16:06
     */
    @Transactional
    public ReturnObject createOrders(Long userId, OrdersVo ordersVo)
    {
        Orders ordersBo = ordersVo.createOrdersBo();
        List<OrderItemsCreateVo> orderItemsVo = ordersVo.getOrderItems();

        // 判断regionId是否有效
        Long regionId = ordersBo.getRegionId();
        if (!addressServiceI.getValidRegionId(regionId).getData())
            new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        // 判断couponId、presaleId、grouponId是否有效  可能为null或是0
        if (!goodsService.judgeActivityIdValid(ordersBo.getCouponId(),ordersBo.getPresaleId(),ordersBo.getGrouponId()).getData())
            new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        // 通过orderItemsVo找到全的
        List<OrderItems> orderItemsList = new ArrayList<OrderItems>();
        List<Integer> countList = new ArrayList<Integer>();
        List<Long> skuIdList = new ArrayList<Long>();
        List<Long> regionIdList = new ArrayList<Long>();

        Long origin_price = 0L;

        for (OrderItemsCreateVo vo: orderItemsVo){
            if (!goodsService.judgeCouponActivityIdValid(ordersBo.getCouponActivityId()).getData())
                new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            GoodsDetailDTO goodsDetailDTO = goodsService.getGoodsBySkuId(vo.getGoodsSkuId()).getData();
            if (goodsDetailDTO == null)
                new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            // 如果库存不够
            if (goodsDetailDTO.getInventory() == null || goodsDetailDTO.getInventory() < vo.getQuantity())
                new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
//            GoodsDetailDTO goodsDetailDTO = new GoodsDetailDTO("caixin", 123L, 10);
            OrderItems orderItems = new OrderItems(vo);
            orderItems.setName(goodsDetailDTO.getName());
            orderItems.setPrice(goodsDetailDTO.getPrice());
            origin_price += goodsDetailDTO.getPrice();
            orderItemsList.add(orderItems);

            countList.add(vo.getQuantity());
            skuIdList.add(vo.getSkuId());
            regionIdList.add(regionId);
        }
        // 算运费
        Integer freight_Price = freightServiceI.calcuFreightPrice(countList, skuIdList, regionIdList).getData();
        ordersBo.setFreightPrice(freight_Price.longValue());

        // 初始价格
        ordersBo.setOriginPrice(origin_price);

        // 算discountPrice

        // 算orderSn
        ordersBo.setOrderSn(Common.genSeqNum().substring(0,13));

        // 设置订单类型
        Byte orderType = 0;
        if (ordersBo.getPresaleId() != null && ordersBo.getPresaleId() != 0)
            orderType = 2;
        else if (ordersBo.getGrouponId() != null && ordersBo.getGrouponId() != 0)
            orderType = 1;
        ordersBo.setOrderType(orderType);

        // 设置状态码
        Orders.State state = Orders.State.CREATE_ORDER;
        ordersBo.setState(state.getCode().byteValue());

        ordersBo.setGmtCreated(LocalDateTime.now());

        CustomerDTO customerDTO = aftersaleServiceI.findCustomerByUserId(userId).getData();
        CustomerRetVo customerRetVo = new CustomerRetVo();
        customerRetVo.setId(userId);
        customerRetVo.setName(customerDTO.getName());
        customerRetVo.setUserName(customerDTO.getUserName());

        ShopDetailDTO shopDetailDTO = goodsService.getShopInfoBySkuId(orderItemsVo.get(0).getGoodsSkuId()).getData();
        ShopRetVo shopRetVo = new ShopRetVo();
        shopRetVo.setId(shopDetailDTO.getShopId());
        shopRetVo.setGmtCreate(shopDetailDTO.getGmtCreate());
        shopRetVo.setGmtModified(shopDetailDTO.getGmtModified());
        shopRetVo.setName(shopDetailDTO.getName());
        shopRetVo.setState(shopDetailDTO.getState());

        ordersBo.setCustomerId(userId);
        ordersBo.setShopId(shopDetailDTO.getShopId());
        ReturnObject<Orders> orders = orderDao.createOrders(ordersBo, orderItemsList);
        OrderCreateRetVo orderCreateRetVo = new OrderCreateRetVo(orders.getData());
        orderCreateRetVo.setCustomerRetVo(customerRetVo);
        orderCreateRetVo.setShopRetVo(shopRetVo);

        return new ReturnObject<>(orderCreateRetVo);
    }
}
