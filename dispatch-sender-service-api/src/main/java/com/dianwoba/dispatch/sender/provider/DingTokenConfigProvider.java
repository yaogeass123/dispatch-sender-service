package com.dianwoba.dispatch.sender.provider;

import com.dianwoba.dispatch.sender.domain.dto.param.token.DingTokenDeleteDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.token.DingTokenQueryDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.token.DingTokenSaveDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.token.DingTokenUpdateDTO;
import com.dianwoba.dispatch.sender.domain.dto.result.DingTokenInfoDTO;
import com.sun.org.apache.xpath.internal.operations.Bool;
import java.util.List;

/**
 * @author Polais
 */
public interface DingTokenConfigProvider {

    /**
     * 保存
     *
     * @param saveDTO DingTokenSaveDTO
     */
    void save(DingTokenSaveDTO saveDTO);


    /**
     * 更新
     *
     * @param updateDTO DingTokenUpdateDTO
     */
    Boolean update(DingTokenUpdateDTO updateDTO);

    /**
     * 删除
     *
     * @param deleteDTO DingTokenDeleteDTO
     */
    Boolean delete(DingTokenDeleteDTO deleteDTO);

    /**
     * 查询
     *
     * @param queryDTO DingTokenQueryDTO
     * @return List
     */
    List<DingTokenInfoDTO> query(DingTokenQueryDTO queryDTO);
}
