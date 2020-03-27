package top.zrbcool.pepper.boot.samples.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;
import top.zrbcool.pepper.boot.rocketmq.consumer.EnhancedDefaultMQPushConsumer;
import top.zrbcool.pepper.boot.rocketmq.consumer.RocketMQConsumerRefer;

import javax.annotation.PostConstruct;

/**
 * @author zhangrongbincool@163.com
 * @version 19-12-31
 */
@Slf4j
@Component
public class SampleConsumer {

    @RocketMQConsumerRefer
    private EnhancedDefaultMQPushConsumer consumer;

    @PostConstruct
    void init() {
        try {
            consumer.subscribe("PEPPER-TEST-TOPIC");
            consumer.setConsumerGroup("default");
            consumer.setMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                for (MessageExt msg : msgs) {
//                    log.info("msg: {}", msg.toString());
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
            consumer.start();
        } catch (MQClientException e) {
            log.error("", e);
        }
    }
}
