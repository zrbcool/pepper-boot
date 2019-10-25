package top.zrbcool.pepper.boot.jedis;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.Map;
import java.util.Objects;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-24
 */
public class BeanRegistrationUtil {
    public static boolean registerBeanDefinitionIfNotExists(BeanDefinitionRegistry registry, String beanName,
                                                            Class<?> beanClass) {
        return registerBeanDefinitionIfNotExists(
                registry, beanName,
                beanClass, null,
                true, true,
                AbstractBeanDefinition.AUTOWIRE_BY_NAME);
    }

    public static boolean registerBeanDefinitionIfBeanNameNotExists(BeanDefinitionRegistry registry, String beanName,
                                                                    Class<?> beanClass) {
        return registerBeanDefinitionIfNotExists(
                registry, beanName,
                beanClass, null,
                false, true,
                AbstractBeanDefinition.AUTOWIRE_BY_NAME);
    }

    public static boolean registerBeanDefinitionIfNotExists(
            BeanDefinitionRegistry registry,
            String beanName,
            Class<?> beanClass,
            Map<String, Object> extraPropertyValues,
            boolean checkClassExist,
            boolean checkBeanNameExist,
            int autowireMode) {
        if (checkBeanNameExist) {
            if (registry.containsBeanDefinition(beanName)) {
                return false;
            }
        }

        if (checkClassExist) {
            String[] candidates = registry.getBeanDefinitionNames();

            for (String candidate : candidates) {
                BeanDefinition beanDefinition = registry.getBeanDefinition(candidate);
                if (Objects.equals(beanDefinition.getBeanClassName(), beanClass.getName())) {
                    return false;
                }
            }
        }

        BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(beanClass).getBeanDefinition();
        ((AbstractBeanDefinition) beanDefinition).setAutowireMode(autowireMode);
        if (extraPropertyValues != null) {
            for (Map.Entry<String, Object> entry : extraPropertyValues.entrySet()) {
                beanDefinition.getPropertyValues().add(entry.getKey(), entry.getValue());
            }
        }

        registry.registerBeanDefinition(beanName, beanDefinition);

        return true;
    }


}
