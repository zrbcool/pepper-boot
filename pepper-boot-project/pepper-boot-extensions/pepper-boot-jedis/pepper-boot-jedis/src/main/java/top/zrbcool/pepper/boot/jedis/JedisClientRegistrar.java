package top.zrbcool.pepper.boot.jedis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import top.zrbcool.pepper.boot.core.AbstractRegistrar;
import top.zrbcool.pepper.boot.util.BeanRegistrationUtil;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-24
 */
public class JedisClientRegistrar extends AbstractRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        final String annotationName = EnableJedisClient.class.getName();
        final String annotationsName = EnableJedisClients.class.getName();
        registerBeanDefinitions(importingClassMetadata, registry, annotationName, annotationsName);
    }

    protected void dealOne(BeanDefinitionRegistry registry, AnnotationAttributes oneAttributes) {
        String namespace = oneAttributes.getString("namespace");
        Assert.isTrue(StringUtils.isNotEmpty(namespace), "namespace must be specified!");
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry,
                namespace + JedisClient.class.getSimpleName(),
                JedisClientFactoryBean.class
        );
    }
}
