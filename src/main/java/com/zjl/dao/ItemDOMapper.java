package com.zjl.dao;

import com.zjl.dataobject.ItemDO;

import java.util.List;

public interface ItemDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Wed Jul 26 21:57:24 CST 2023
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Wed Jul 26 21:57:24 CST 2023
     */
    int insert(ItemDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Wed Jul 26 21:57:24 CST 2023
     */
    int insertSelective(ItemDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Wed Jul 26 21:57:24 CST 2023
     */
    ItemDO selectByPrimaryKey(Integer id);

    List<ItemDO> listItem();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Wed Jul 26 21:57:24 CST 2023
     */
    int updateByPrimaryKeySelective(ItemDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Wed Jul 26 21:57:24 CST 2023
     */
    int updateByPrimaryKey(ItemDO record);

    int increaseSales(Integer id, Integer amount);
}