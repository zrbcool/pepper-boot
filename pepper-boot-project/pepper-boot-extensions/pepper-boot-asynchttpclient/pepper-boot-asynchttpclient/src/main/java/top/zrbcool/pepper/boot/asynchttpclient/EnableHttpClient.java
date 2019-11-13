package top.zrbcool.pepper.boot.asynchttpclient;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-11
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableHttpClients.class)
@Import(HttpClientRegistrar.class)
public @interface EnableHttpClient {
    String namespace() default "default";
}