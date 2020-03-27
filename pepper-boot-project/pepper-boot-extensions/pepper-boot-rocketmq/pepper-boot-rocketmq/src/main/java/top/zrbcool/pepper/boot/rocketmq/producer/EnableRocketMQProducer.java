package top.zrbcool.pepper.boot.rocketmq.producer;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author zhangrongbincool@163.com
 * @version 19-12-30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableRocketMQProducers.class)
@Import(RocketMQProducerRegistrar.class)
public @interface EnableRocketMQProducer {
    String namespace() default "default";
    String producerGroup() default "";
}