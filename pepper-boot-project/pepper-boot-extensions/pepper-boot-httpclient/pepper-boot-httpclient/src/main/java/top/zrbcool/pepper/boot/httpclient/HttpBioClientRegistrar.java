package top.zrbcool.pepper.boot.httpclient;

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
 * @version 19-11-11
 */
public class HttpBioClientRegistrar extends AbstractRegistrar implements ImportBeanDefinitionRegistrar {


    protected void dealOne(BeanDefinitionRegistry registry, AnnotationAttributes oneAttributes) {
        String namespace = oneAttributes.getString("namespace");
        Assert.isTrue(StringUtils.isNotEmpty(namespace), "namespace must be specified!");
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry, namespace + HttpBioClient.class.getSimpleName(), HttpBioClientFactoryBean.class);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        final String annotationName = EnableHttpBioClient.class.getName();
        final String annotationsName = EnableHttpBioClients.class.getName();
        registerBeanDefinitions(importingClassMetadata, registry, annotationName, annotationsName);
    }
}
