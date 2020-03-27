package top.zrbcool.pepper.boot.mybatis;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-11
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableDataSources.class)
@Import(DataSourceRegistrar.class)
public @interface EnableDataSource {
    String namespace() default "default";
    String[] mapperPackages() default {};
}