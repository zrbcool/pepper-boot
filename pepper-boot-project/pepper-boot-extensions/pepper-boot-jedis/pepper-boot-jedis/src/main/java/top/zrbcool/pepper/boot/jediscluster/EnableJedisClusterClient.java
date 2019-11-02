package top.zrbcool.pepper.boot.jediscluster;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;
/**
 * @author zhangrongbincool@163.com
 * @version 19-11-1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableJedisClusterClients.class)
@Import(JedisClusterClientRegistrar.class)
public @interface EnableJedisClusterClient {
    String namespace() default "default";
}