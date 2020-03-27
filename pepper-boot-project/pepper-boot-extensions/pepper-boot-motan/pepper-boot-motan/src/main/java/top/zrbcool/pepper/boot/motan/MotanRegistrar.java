package top.zrbcool.pepper.boot.motan;

import com.weibo.api.motan.config.springsupport.*;
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
 * @version 19-11-7
 */
public class MotanRegistrar extends AbstractRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        final String annotationName = EnableMotan.class.getName();
        final String annotationsName = EnableMotans.class.getName();
        registerBeanDefinitions(importingClassMetadata, registry, annotationName, annotationsName);
    }

    protected void dealOne(BeanDefinitionRegistry registry, AnnotationAttributes oneAttributes) {
        String namespace = oneAttributes.getString("namespace");
        Assert.isTrue(StringUtils.isNotEmpty(namespace), "namespace must be specified!");
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + MotanBeanPostProcessor.class.getSimpleName(), MotanBeanPostProcessor.class
        );
//        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
//                registry, AnnotationBean.class.getSimpleName(), AnnotationBean.class
//        );
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + RegistryConfigBean.class.getSimpleName(), RegistryConfigBean.class
        );
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + ProtocolConfigBean.class.getSimpleName(), ProtocolConfigBean.class
        );
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + BasicServiceConfigBean.class.getSimpleName(), BasicServiceConfigBean.class
        );
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + BasicRefererConfigBean.class.getSimpleName(), BasicRefererConfigBean.class
        );
    }

}
