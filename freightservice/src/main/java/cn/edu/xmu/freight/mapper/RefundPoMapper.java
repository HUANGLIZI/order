package cn.edu.xmu.freight.mapper;

import cn.edu.xmu.freight.model.po.RefundPo;
import cn.edu.xmu.freight.model.po.RefundPoExample;
import java.util.List;

public interface RefundPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table refund
     *
     * @mbg.generated
     */
    int deleteByExample(RefundPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table refund
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table refund
     *
     * @mbg.generated
     */
    int insert(RefundPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table refund
     *
     * @mbg.generated
     */
    int insertSelective(RefundPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table refund
     *
     * @mbg.generated
     */
    List<RefundPo> selectByExample(RefundPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table refund
     *
     * @mbg.generated
     */
    RefundPo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table refund
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(RefundPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table refund
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(RefundPo record);
}