package cn.edu.xmu.log.mapper;

import cn.edu.xmu.log.model.po.LogPo;

public interface LogPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_log
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_log
     *
     * @mbg.generated
     */
    int insert(LogPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_log
     *
     * @mbg.generated
     */
    int insertSelective(LogPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_log
     *
     * @mbg.generated
     */
    LogPo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_log
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(LogPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_log
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(LogPo record);
}