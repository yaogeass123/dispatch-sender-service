package com.dianwoba.dispatch.sender.provider;

import com.dianwoba.dispatch.sender.domain.dto.param.dep.AppDepQueryDTO;
import com.dianwoba.dispatch.sender.domain.dto.result.AppDepInfoDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.dep.AppDepManualDepUpdateDTO;
import java.util.List;

/**
 * @author Polaris
 */
public interface AppDepConfigProvider {

    /**
     * 手动更改部门id
     * @param updateDTO AppDepManualDepUpdateDTO
     * @return Boolean
     */
    Boolean updateManualDep(AppDepManualDepUpdateDTO updateDTO);

    /**
     * 查询某群组下全部 app信息
     * @param queryDTO AppDepQueryDTO
     * @return List<AppDepInfoDTO>
     */
    List<AppDepInfoDTO> queryAppInfo(AppDepQueryDTO queryDTO);


}
