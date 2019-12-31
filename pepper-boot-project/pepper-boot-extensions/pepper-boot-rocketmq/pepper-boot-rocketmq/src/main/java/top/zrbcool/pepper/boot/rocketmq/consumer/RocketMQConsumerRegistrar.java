package top.zrbcool.pepper.boot.rocketmq.consumer;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import top.zrbcool.pepper.boot.core.AbstractRegistrar;
import top.zrbcool.pepper.boot.rocketmq.producer.EnableRocketMQProducer;
import top.zrbcool.pepper.boot.rocketmq.producer.EnableRocketMQProducers;
import top.zrbcool.pepper.boot.rocketmq.producer.EnhancedDefaultMQProducer;
import top.zrbcool.pepper.boot.rocketmq.producer.RocketMQProducerFactoryBean;
import top.zrbcool.pepper.boot.util.BeanRegistrationUtil;

import java.util.Map;

/**
 * @author zhangrongbincool@163.com
 * @version 19-12-30
 */
public class RocketMQConsumerRegistrar extends AbstractRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        final String annotationName = EnableRocketMQConsumer.class.getName();
        final String annotationsName = EnableRocketMQConsumers.class.getName();
        registerBeanDefinitions(importingClassMetadata, registry, annotationName, annotationsName);
    }

    protected void dealOne(BeanDefinitionRegistry registry, AnnotationAttributes oneAttributes) {
        String namespace = oneAttributes.getString("namespace");
        Assert.isTrue(StringUtils.isNotEmpty(namespace), "namespace must be specified!");
        Map<String, Object> extraPropertyValues = Maps.newHashMap();
        extraPropertyValues.put("namespace", namespace);

        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry,
                namespace + EnhancedDefaultMQPushConsumer.class.getSimpleName(),
                extraPropertyValues,
                RocketMQConsumerFactoryBean.class
        );
    }
}
