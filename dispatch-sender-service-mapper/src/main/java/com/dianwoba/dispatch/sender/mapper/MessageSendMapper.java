package com.dianwoba.dispatch.sender.mapper;

import com.dianwoba.dispatch.sender.entity.MessageSend;
import com.dianwoba.dispatch.sender.entity.MessageSendExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MessageSendMapper {
    long countByExample(MessageSendExample example);

    int deleteByExample(MessageSendExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MessageSend record);

    int insertSelective(MessageSend record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_send
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<MessageSend> selectByExampleSelective(@Param("example") MessageSendExample example, @Param("selective") MessageSend.Column ... selective);

    List<MessageSend> selectByExample(MessageSendExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_send
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    MessageSend selectByPrimaryKeySelective(@Param("id") Long id, @Param("selective") MessageSend.Column ... selective);

    MessageSend selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MessageSend record, @Param("example") MessageSendExample example);

    int updateByExample(@Param("record") MessageSend record, @Param("example") MessageSendExample example);

    int updateByPrimaryKeySelective(MessageSend record);

    int updateByPrimaryKey(MessageSend record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_send
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int batchInsert(@Param("list") List<MessageSend> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_send
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int batchInsertSelective(@Param("list") List<MessageSend> list, @Param("selective") MessageSend.Column ... selective);
}