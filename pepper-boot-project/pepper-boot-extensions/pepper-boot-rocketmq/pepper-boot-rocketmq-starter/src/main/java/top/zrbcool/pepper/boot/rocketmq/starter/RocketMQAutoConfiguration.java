package top.zrbcool.pepper.boot.rocketmq.starter;

/**
 * @author zhangrongbincool@163.com
 * @version 19-12-31
 */

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import top.zrbcool.pepper.boot.rocketmq.consumer.RocketMQConsumerAnnotationBean;
import top.zrbcool.pepper.boot.rocketmq.producer.RocketMQProducerAnnotationBean;

/**
 * @author zhangrongbincool@163.com
 * @version 19-9-24
 */
@ConditionalOnClass({
        DefaultMQProducer.class,
        DefaultMQPushConsumer.class
})
public class RocketMQAutoConfiguration {
    @Bean
    public RocketMQConsumerAnnotationBean rocketMQConsumerAnnotationBean() {
        return new RocketMQConsumerAnnotationBean();
    }

    @Bean
    RocketMQProducerAnnotationBean rocketMQProducerAnnotationBean() {
        return new RocketMQProducerAnnotationBean();
    }
}
