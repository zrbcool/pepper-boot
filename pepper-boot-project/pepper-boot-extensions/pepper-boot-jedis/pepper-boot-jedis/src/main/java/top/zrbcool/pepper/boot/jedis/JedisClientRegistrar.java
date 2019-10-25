package top.zrbcool.pepper.boot.jedis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-24
 */
public class JedisClientRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean processed = false;
        {
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata
                    .getAnnotationAttributes(EnableJedisClient.class.getName()));
            if (attributes != null) {
                dealOne(registry, attributes);
                processed = true;
            }
        }
        {
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata
                    .getAnnotationAttributes(EnableJedisClients.class.getName()));
            if (attributes != null) {
                AnnotationAttributes[] annotationArray = attributes.getAnnotationArray("value");
                for (AnnotationAttributes oneAttributes : annotationArray) {
                    dealOne(registry, oneAttributes);
                    processed = true;
                }
            }
        }
        if (!processed)
            throw new IllegalStateException("no @EnableJedisClient or @EnableJedisClients found! pls check!");
    }

    private void dealOne(BeanDefinitionRegistry registry, AnnotationAttributes oneAttributes) {
        String namespace = oneAttributes.getString("namespace");
        Assert.isTrue(StringUtils.isNotEmpty(namespace), "namespace must be specified!");
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry, namespace + JedisClient.class.getSimpleName(), JedisClientFactoryBean.class);
    }

}
