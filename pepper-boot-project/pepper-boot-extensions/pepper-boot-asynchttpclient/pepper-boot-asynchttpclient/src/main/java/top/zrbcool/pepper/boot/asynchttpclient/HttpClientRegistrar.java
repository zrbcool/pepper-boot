package top.zrbcool.pepper.boot.asynchttpclient;

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
 * @version 19-11-13
 */
public class HttpClientRegistrar extends AbstractRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        final String annotationName = EnableHttpClient.class.getName();
        final String annotationsName = EnableHttpClients.class.getName();
        registerBeanDefinitions(importingClassMetadata, registry, annotationName, annotationsName);
    }

    protected void dealOne(BeanDefinitionRegistry registry, AnnotationAttributes oneAttributes) {
        String namespace = oneAttributes.getString("namespace");
        Assert.isTrue(StringUtils.isNotEmpty(namespace), "namespace must be specified!");
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry, namespace + HttpClient.class.getSimpleName(), HttpClientFactoryBean.class);
    }

}
