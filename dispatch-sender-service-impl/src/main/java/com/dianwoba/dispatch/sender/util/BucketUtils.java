package com.dianwoba.dispatch.sender.util;

import com.dianwoba.dispatch.sender.constant.Constant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.assertj.core.util.Lists;

/**
 * @author Polaris
 */
public class BucketUtils {

    public static List<Long> buildTokenQueue(Set<Long> buckets) {
        List<Long> tokenQueue = Lists.newArrayList();
        for (int i = 0; i< Constant.MAX_SENT_TIMES_ONE_TOKEN; i++) {
            tokenQueue.addAll(buckets);
        }
        return tokenQueue;
    }

    public static List<Long> buildTokenQueue(String buckets) {
        String[] strings = buckets.split("-");
        List<Long> tokenQueue = Lists.newArrayList();
        for (String str : strings) {
            tokenQueue.add(Long.valueOf(str));
        }
        return tokenQueue;
    }

    public static String buildBucketString(List<Long> tokenQueue) {
        return tokenQueue.stream().map(String::valueOf).collect(Collectors.joining("-"));
    }

    public static String buildBucketString(Set<Long> buckets) {
        List<Long> tokenQueue = buildTokenQueue(buckets);
        return buildBucketString(tokenQueue);
    }
}
