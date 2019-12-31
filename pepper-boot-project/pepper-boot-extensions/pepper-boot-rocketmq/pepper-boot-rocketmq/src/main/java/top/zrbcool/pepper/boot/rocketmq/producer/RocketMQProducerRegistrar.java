package top.zrbcool.pepper.boot.rocketmq.producer;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import top.zrbcool.pepper.boot.core.AbstractRegistrar;
import top.zrbcool.pepper.boot.util.BeanRegistrationUtil;

import java.util.Map;

/**
 * @author zhangrongbincool@163.com
 * @version 19-12-30
 */
public class RocketMQProducerRegistrar extends AbstractRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        final String annotationName = EnableRocketMQProducer.class.getName();
        final String annotationsName = EnableRocketMQProducers.class.getName();
        registerBeanDefinitions(importingClassMetadata, registry, annotationName, annotationsName);
    }

    protected void dealOne(BeanDefinitionRegistry registry, AnnotationAttributes oneAttributes) {
        String namespace = oneAttributes.getString("namespace");
        Assert.isTrue(StringUtils.isNotEmpty(namespace), "namespace must be specified!");
        Map<String, Object> extraPropertyValues = Maps.newHashMap();
        extraPropertyValues.put("producerGroup", oneAttributes.getString("producerGroup"));
        extraPropertyValues.put("namespace", namespace);

        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry,
                namespace + EnhancedDefaultMQProducer.class.getSimpleName(),
                extraPropertyValues,
                RocketMQProducerFactoryBean.class
        );
    }
}
