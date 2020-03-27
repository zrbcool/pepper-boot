package top.zrbcool.pepper.boot.rocketmq.consumer;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhangrongbincool@163.com
 * @version 19-12-30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(RocketMQConsumerRegistrar.class)
public @interface EnableRocketMQConsumers {
	EnableRocketMQConsumer[] value();
}