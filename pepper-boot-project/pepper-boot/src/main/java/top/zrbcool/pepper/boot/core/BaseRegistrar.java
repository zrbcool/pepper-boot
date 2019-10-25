package top.zrbcool.pepper.boot.core;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-25
 */
public abstract class BaseRegistrar {

    protected void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, String annotationName, String annotationsName) {
        boolean processed = false;
        {
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata
                    .getAnnotationAttributes(annotationName));
            if (attributes != null) {
                dealOne(registry, attributes);
                processed = true;
            }
        }
        {
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata
                    .getAnnotationAttributes(annotationsName));
            if (attributes != null) {
                AnnotationAttributes[] annotationArray = attributes.getAnnotationArray("value");
                for (AnnotationAttributes oneAttributes : annotationArray) {
                    dealOne(registry, oneAttributes);
                    processed = true;
                }
            }
        }
        if (!processed)
            throw new IllegalStateException(String.format("no @%s or @%s found! pls check!", annotationName, annotationsName));
    }

    protected abstract void dealOne(BeanDefinitionRegistry registry, AnnotationAttributes attributes);
}
