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

    Integer FIFTY = 50;

    String AT_ALL = "atAll";

    String SUCCESS = "success";

    String DING_URL_PRE = "https://oapi.dingtalk.com/robot/send?access_token=";

    Integer HTTP_OK = 200;

    Integer DING_PARAM_ERROR = 300001;

    Integer DING_VALID_ERROR = 310000;

    String DING_MESSAGE = "DingMessage";

    String MAIL_SUBJECT_SEND_ERROR = "钉钉消息发送失败告警";

    String MAIL_SUBJECT_IGNORE = "钉钉消息忽略通知";

    String MAIL_SUBJECT_STATISTIC = "钉钉消息忽略与失败统计";

    String MAIL_SUBJECT_NOT_EXIST = "钉钉无配置机器人告警";

    Byte TOKEN_ERROR = 1;

    Byte TOKEN_NORMAL = 0;

    Integer BATCH_INSERT_MAX_SIZE = 500;

    String DATE_FORMAT = "HH:mm:ss";

    String DEFAULT_STAFF = "automate";

    String PREFIX_LINK_DEPLOY_PLATFORM = "http://new-robert-service.nidianwo.com/api/app/getModifyApp?modifyTime=";

}
