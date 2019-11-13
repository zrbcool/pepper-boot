package top.zrbcool.pepper.boot.httpclient;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-11
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableHttpBioClients.class)
@Import(HttpBioClientRegistrar.class)
public @interface EnableHttpBioClient {
    String namespace() default "default";
}