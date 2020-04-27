package com.dianwoba.dispatch.sender.provider;

import com.dianwoba.dispatch.sender.domain.dto.param.group.GroupDeleteDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.group.GroupPagingQueryDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.group.GroupSaveDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.group.GroupUpdateDTO;
import com.dianwoba.dispatch.sender.domain.dto.result.GroupInfoDTO;
import com.dianwoba.wireless.paging.Pagination;

/**
 * @author Polaris
 */
public interface GroupConfigProvider {

    /**
     * 新增群配置
     * @param saveDTO GroupSaveDTO
     */
    void save(GroupSaveDTO saveDTO);


    /**
     * 更新群配置
     * @param UpdateDTO GroupUpdateDTO
     * @return Boolean
     */
    Boolean update(GroupUpdateDTO UpdateDTO);

    /**
     * 删除某一群配置（会删除该群下相应的钉钉机器人配置）
     * @param deleteDTO GroupDeleteDTO
     * @return Boolean
     */
    Boolean delete(GroupDeleteDTO deleteDTO);

    /**
     * 分页查询
     * @param queryDTO GroupPagingQueryDTO
     * @return Pagination<GroupInfoDTO>
     */
    Pagination<GroupInfoDTO> query(GroupPagingQueryDTO queryDTO);

    /**
     * 查询总数量
     * @return LOng
     */
    Long totalCount();

}
