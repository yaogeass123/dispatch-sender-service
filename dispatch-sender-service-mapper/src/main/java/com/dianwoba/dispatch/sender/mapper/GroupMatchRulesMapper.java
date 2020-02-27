package com.dianwoba.dispatch.sender.mapper;

import com.dianwoba.dispatch.sender.entity.GroupMatchRules;
import com.dianwoba.dispatch.sender.entity.GroupMatchRulesExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GroupMatchRulesMapper {
    long countByExample(GroupMatchRulesExample example);

    int deleteByExample(GroupMatchRulesExample example);

    int deleteByPrimaryKey(Long id);

    int insert(GroupMatchRules record);

    int insertSelective(GroupMatchRules record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table group_match_rules
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<GroupMatchRules> selectByExampleSelective(@Param("example") GroupMatchRulesExample example, @Param("selective") GroupMatchRules.Column ... selective);

    List<GroupMatchRules> selectByExample(GroupMatchRulesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table group_match_rules
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    GroupMatchRules selectByPrimaryKeySelective(@Param("id") Long id, @Param("selective") GroupMatchRules.Column ... selective);

    GroupMatchRules selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") GroupMatchRules record, @Param("example") GroupMatchRulesExample example);

    int updateByExample(@Param("record") GroupMatchRules record, @Param("example") GroupMatchRulesExample example);

    int updateByPrimaryKeySelective(GroupMatchRules record);

    int updateByPrimaryKey(GroupMatchRules record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table group_match_rules
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int batchInsert(@Param("list") List<GroupMatchRules> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table group_match_rules
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int batchInsertSelective(@Param("list") List<GroupMatchRules> list, @Param("selective") GroupMatchRules.Column ... selective);
}