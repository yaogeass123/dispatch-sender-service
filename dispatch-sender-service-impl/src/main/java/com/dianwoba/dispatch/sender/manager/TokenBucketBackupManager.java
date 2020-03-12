package com.dianwoba.dispatch.sender.manager;

import com.dianwoba.dispatch.sender.entity.TokenBucketBackup;
import com.dianwoba.dispatch.sender.mapper.TokenBucketBackupMapper;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */
@Component
public class TokenBucketBackupManager {

    @Resource
    private TokenBucketBackupMapper tokenBucketBackupMapper;

    public TokenBucketBackup queryByGroupId(long groupId) {
        return tokenBucketBackupMapper.selectByPrimaryKey((int) groupId);
    }


}
