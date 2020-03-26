package com.dianwoba.dispatch.sender.mapper;

import com.dianwoba.dispatch.sender.entity.MessageSendCountPO;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;


/**
 * @author Polaris
 */
public interface MessageSendMapperExt {

    /**
     * 根据状态统计messageSend中分组信息数量
     * @param status 状态
     * @return map
     */
    List<MessageSendCountPO> countByGroupId(@Param("status")Byte status);
}
