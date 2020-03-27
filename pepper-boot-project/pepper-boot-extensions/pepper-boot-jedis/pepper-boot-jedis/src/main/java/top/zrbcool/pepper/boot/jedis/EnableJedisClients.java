package top.zrbcool.pepper.boot.jedis;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(JedisClientRegistrar.class)
public @interface EnableJedisClients {
    EnableJedisClient[] value();
}