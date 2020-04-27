package com.dianwoba.dispatch.sender.util;

import com.dianwoba.dispatch.sender.domain.dto.param.dep.AppDepManualDepUpdateDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.MessageSendDTO;
import com.dianwoba.dispatch.sender.exception.ConfigException;

/**
 * @author Polaris
 */
public class ValidUtils {

    public static boolean validMessageDTO(MessageSendDTO dto) {

        return true;
    }

    public static void validAppDepManualDepUpdateDTO(AppDepManualDepUpdateDTO updateDTO) {
        if (updateDTO.getAppName() == null) {
            throw new ConfigException("应用名不能为空");
        }
        if (updateDTO.getDeFaultDepId() == null) {
            throw new ConfigException("默认部门不能为空");
        }
        if (updateDTO.getNewDepId() == null) {
            throw new ConfigException("新部门不能为空");
        }
        if (updateDTO.getModifier() == null) {
            throw new ConfigException("修改人不能为空");
        }
    }

}
