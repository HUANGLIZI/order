package cn.edu.xmu.ordermodule.mapper;

import cn.edu.xmu.ordermodule.model.po.OrderItemPo;
import cn.edu.xmu.ordermodule.model.po.OrderItemPoExample;
import java.util.List;

public interface OrderItemPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated
     */
    int deleteByExample(OrderItemPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated
     */
    int insert(OrderItemPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated
     */
    int insertSelective(OrderItemPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated
     */
    List<OrderItemPo> selectByExample(OrderItemPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated
     */
    OrderItemPo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(OrderItemPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(OrderItemPo record);
}