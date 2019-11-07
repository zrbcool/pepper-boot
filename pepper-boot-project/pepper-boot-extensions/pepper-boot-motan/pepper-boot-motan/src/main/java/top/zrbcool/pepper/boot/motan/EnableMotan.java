package top.zrbcool.pepper.boot.motan;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-7
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableMotans.class)
@Import(MotanRegistrar.class)
public @interface EnableMotan {
    String namespace() default "default";
}