package top.zrbcool.pepper.boot.jedis;

import java.lang.annotation.*;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-24
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface JedisClientRefer {
    String namespace() default "default";
}
