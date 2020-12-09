package cn.edu.xmu.order.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.order.model.po.OrdersPo;
import cn.edu.xmu.order.model.vo.OrderRetVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单Bo类
 **/
@Data
public class Orders implements VoObject, Serializable {
    private Long id;
    private Long customerId;
    private Long shopId;
    private String orderSn;
    private Long pid;
    private String consignee;
    private Long regionId;
    private String address;
    private String mobile;
    private String message;
    private Byte orderType;
    private Long freightPrice;
    private Long couponId;
    private Long couponActivityId;
    private Long discountPrice;
    private Long originPrice;
    private Long presaleId;
    private Long grouponDiscount;
    private Integer rebateNum;
    private LocalDateTime confirmTime;
    private String shipmentSn;
    private Byte state;
    private Byte substate;
    private Byte beDeleted;
    private Long grouponId;
    private LocalDateTime gmtCreated;
    private LocalDateTime gmtModified;
    private List<OrderItems> orderItemsList;

    public Orders() {
    }

    /**
     * 构造函数
     *
     * @param po 用PO构造
     * @return Orders
     */
    public Orders(OrdersPo po) {
        this.id = po.getId();
        this.customerId=po.getCustomerId();
        this.shopId=po.getShopId();
        this.orderSn=po.getOrderSn();
        this.pid=po.getPid();
        this.consignee = po.getConsignee();
        this.regionId=po.getRegionId();
        this.address = po.getAddress();
        this.mobile=po.getMobile();
        this.message=po.getMessage();
        this.orderType=po.getOrderType();
        this.freightPrice=po.getFreightPrice();
        this.couponId=po.getCouponId();
        this.couponActivityId=po.getCouponActivityId();
        this.discountPrice=po.getDiscountPrice();
        this.originPrice=po.getOriginPrice();
        this.grouponDiscount=po.getGrouponDiscount();
        this.rebateNum=po.getRebateNum();
        this.confirmTime = po.getConfirmTime();
        this.shipmentSn=po.getShipmentSn();
        this.beDeleted = po.getBeDeleted();
        this.state=po.getState();
        this.gmtCreated = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
        this.presaleId=po.getPresaleId();
        this.substate=po.getSubstate();
        this.grouponId=po.getGrouponId();
    }

    @Override
    public Object createVo() {
        return new OrderRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
    public OrdersPo gotOrdersPo() {
        OrdersPo po = new OrdersPo();
        po.setGrouponId(grouponId);
        po.setId(id);
        po.setCustomerId(customerId);
        po.setShopId(shopId);
        po.setOrderSn(orderSn);
        po.setPid(pid);
        po.setConsignee(consignee);
        po.setRegionId(regionId);
        po.setAddress(address);
        po.setMobile(mobile);
        po.setMessage(message);
        po.setOrderType(orderType);
        po.setFreightPrice(freightPrice);
        po.setCouponId(couponId);
        po.setCouponActivityId(couponActivityId);
        po.setDiscountPrice(discountPrice);
        po.setOriginPrice(originPrice);
        po.setPresaleId(presaleId);
        po.setGrouponDiscount(grouponDiscount);
        po.setGrouponId(grouponId);
        po.setRebateNum(rebateNum);
        po.setConfirmTime(confirmTime);
        po.setShipmentSn(shipmentSn);
        po.setState(state);
        po.setSubstate(substate);
        po.setBeDeleted(beDeleted);
        po.setGmtCreate(gmtCreated);
        po.setGmtModified(gmtModified);
        return po;
    }

    /**
     * 后台用户状态
     */
    public enum State {
        CANCEL(0, "订单取消"),
        DEPOSIT_TO_BE_PAID(1, "待支付定金"),
        TO_BE_PAID(2, "待支付"),
        TO_BE_GROUP(3, "待参团"),
        DEPOSIT_HAS_PAID(4, "已支付定金"),
        REST_TO_BE_PAID(5, "待支付尾款"),
        CREATE_ORDER(6, "创建订单"),
        PRESALE_SUSPENSION(7, "预售中止"),
        HAS_JOIN_GROUP(8, "已参团"),
        GROUP_NOT_REACHED_THRESHOLD(9, "团购未达到门槛"),
        GROUP_HAS_FORMED(10, "已成团"),
        HAS_PAID(11, "已支付"),
        REST_HAS_PAID(12, "已支付尾款"),
        HAS_REFUNDED(13, "已退款"),
        ORDER_SUSPENSION(14, "订单中止"),
        AFTERSALE_ORDER_TO_BE_DELIVERED(15, "售后单待发货"),
        DELIVERING(16, "发货中"),
        ARRIVED(17, "到货"),
        HAS_SIGNED_IN(18, "已签收");

        private static final Map<Integer, State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Orders.State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Orders.State getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

}
