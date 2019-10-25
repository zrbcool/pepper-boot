package top.zrbcool.pepper.boot.jedis;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;
/**
 * @author zhangrongbincool@163.com
 * @version 19-10-24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableJedisClients.class)
@Import(JedisClientRegistrar.class)
public @interface EnableJedisClient {
    String namespace() default "default";
    String prefix() default "app.jedis";
}