package top.zrbcool.pepper.boot.rocketmq.consumer;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author zhangrongbincool@163.com
 * @version 19-12-30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableRocketMQConsumers.class)
@Import(RocketMQConsumerRegistrar.class)
public @interface EnableRocketMQConsumer {
	String namespace() default "default";
}