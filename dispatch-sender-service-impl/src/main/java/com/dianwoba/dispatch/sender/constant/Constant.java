package com.dianwoba.dispatch.sender.constant;

/**
 * @author Administrator
 */
public interface Constant {

    String GROUP_COMMON_FORMAT = "%s_%s_%s";

    String HIGH_GATHER_FORMAT = "%s_%s_%s_%s";

    String GROUP_TOKEN_PREFIX = "groupId_%s";

    String REDIS_SEND_TIMES = "redis_groupId_%s";

    String GROUP_NEXT_TOKEN = "groupId_%s_next_token";

    String BACK = "back";

    String ALL = "all";

    Byte LOW = 1;

    Byte MEDIUM = 2;

    Byte HIGH = 3;

    Integer FIFTY = 50;

    String SUCCESS = "success";

    String DING_URL_PRE = "https://oapi.dingtalk.com/robot/send?access_token=";

    Integer HTTP_OK = 200;

    Integer DING_PARAM_ERROR = 300001;

    Integer DING_VALID_ERROR = 310000;

    String DING_MESSAGE = "DingMessage";

    String MAIL_SUBJECT_SEND_ERROR = "钉钉消息发送失败告警";

    String MAIL_SUBJECT_IGNORE = "顶顶消息忽略通知";
}
