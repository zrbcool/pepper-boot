package top.zrbcool.pepper.boot.jediscluster;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(JedisClusterClientRegistrar.class)
public @interface EnableJedisClusterClients {
    EnableJedisClusterClient[] value();
}