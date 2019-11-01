package top.zrbcool.pepper.boot.jedis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-25
 */
public class JedisClientAnnotationBean implements BeanPostProcessor, BeanFactoryAware, EnvironmentAware, InitializingBean {
    private Environment environment;
    private BeanFactory beanFactory;
    private String[] annotationPackages = new String[]{""};

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!isMatchPackage(bean)) {
            return bean;
        }

        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                JedisClientRefer reference = field.getAnnotation(JedisClientRefer.class);
                if (reference != null) {
                    Object value = refer(reference, field.getType());
                    if (value != null) {
                        field.set(bean, value);
                    }
                }
            } catch (Throwable t) {
                throw new BeanInitializationException("Failed to init remote service reference at filed " + field.getName()
                        + " in class " + bean.getClass().getName(), t);
            }
        }
        return bean;
    }

    private boolean isMatchPackage(Object bean) {
        if (annotationPackages == null || annotationPackages.length == 0) {
            return true;
        }
        Class clazz = bean.getClass();
        if (isProxyBean(bean)) {
            clazz = AopUtils.getTargetClass(bean);
        }
        String beanClassName = clazz.getName();
        for (String pkg : annotationPackages) {
            if (beanClassName.startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }

    public String[] getAnnotationPackages() {
        return annotationPackages;
    }

    public void setAnnotationPackages(String[] annotationPackages) {
        this.annotationPackages = annotationPackages;
    }

    private Object refer(JedisClientRefer reference, Class<?> type) {
        return beanFactory.getBean(reference.namespace() + JedisClient.class.getSimpleName(), type);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private boolean isProxyBean(Object bean) {
        return AopUtils.isAopProxy(bean);
    }

    @Override
    public void afterPropertiesSet() {
        final String temp = environment.getProperty("app.jedis.default.annotation.annotationPackages", "");
        this.annotationPackages = StringUtils.split(temp, ',');
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
