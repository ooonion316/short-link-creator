package pers.zyx.shortlink.mq.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import pers.zyx.shortlink.dto.biz.ShortLinkStatsRecordDTO;
import pers.zyx.shortlink.mq.idempotent.MessageQueueIdempotentHandler;
import pers.zyx.shortlink.service.LinkService;

import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

import static pers.zyx.shortlink.constant.LinkGotoRedisKeyConstant.DELAY_QUEUE_STATS_KEY;

/**
 * 延迟记录短链接统计组件
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DelayShortLinkStatsConsumer implements InitializingBean {
    private final MessageQueueIdempotentHandler messageQueueIdempotentHandler;
    private final RedissonClient redissonClient;
    private final LinkService linkService;

    public void onMessage() {
        Executors.newSingleThreadExecutor(
                        runnable -> {
                            Thread thread = new Thread(runnable);
                            thread.setName("delay_short-link_stats_consumer");
                            thread.setDaemon(Boolean.TRUE);
                            return thread;
                        })
                .execute(() -> {
                    RBlockingDeque<ShortLinkStatsRecordDTO> blockingDeque = redissonClient.getBlockingDeque(DELAY_QUEUE_STATS_KEY);
                    RDelayedQueue<ShortLinkStatsRecordDTO> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
                    for (; ; ) {
                        try {
                            ShortLinkStatsRecordDTO statsRecord = delayedQueue.poll();
                            if (statsRecord != null) {
                                if (messageQueueIdempotentHandler.isMessageProcessed(statsRecord.getKeys())) {
                                    if (messageQueueIdempotentHandler.isAccomplish(statsRecord.getKeys())) {
                                        return;
                                    }
                                }
                                try {
                                    linkService.shortLinkStats(null, null, statsRecord);
                                } catch (Throwable ex) {
                                    messageQueueIdempotentHandler.delMessageProcessed(statsRecord.getKeys());
                                    log.error("延迟记录短链接监控消费异常", ex);
                                }
                                messageQueueIdempotentHandler.setAccomplish(statsRecord.getKeys());
                                continue;
                            }
                            LockSupport.parkUntil(500);
                        } catch (Throwable ignored) {
                        }
                    }
                });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        onMessage();
    }
}