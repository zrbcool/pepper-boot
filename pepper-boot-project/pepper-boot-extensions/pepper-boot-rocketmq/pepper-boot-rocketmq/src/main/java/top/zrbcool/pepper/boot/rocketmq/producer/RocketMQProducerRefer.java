package top.zrbcool.pepper.boot.rocketmq.producer;

import java.lang.annotation.*;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-24
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface RocketMQProducerRefer {
    String namespace() default "default";
}
