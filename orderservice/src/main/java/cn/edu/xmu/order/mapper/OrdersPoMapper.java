package cn.edu.xmu.order.mapper;

import cn.edu.xmu.order.model.po.OrdersPo;
import cn.edu.xmu.order.model.po.OrdersPoExample;
import java.util.List;

public interface OrdersPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table orders
     *
     * @mbg.generated
     */
    int deleteByExample(OrdersPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table orders
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table orders
     *
     * @mbg.generated
     */
    int insert(OrdersPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table orders
     *
     * @mbg.generated
     */
    int insertSelective(OrdersPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table orders
     *
     * @mbg.generated
     */
    List<OrdersPo> selectByExample(OrdersPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table orders
     *
     * @mbg.generated
     */
    OrdersPo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table orders
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(OrdersPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table orders
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(OrdersPo record);
}