package top.zrbcool.pepper.boot.core;

import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-1
 */
public abstract class AbstractAnnotationBean<T extends Annotation> implements BeanFactoryAware, EnvironmentAware, InitializingBean {
    protected Environment environment;
    protected BeanFactory beanFactory;
    protected String[] annotationPackages = new String[]{""};
    protected Object postProcessBeforeInitialization(Object bean, String beanName, Class<T> tClass) throws BeansException {
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
                T reference = field.getAnnotation(tClass);
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

    private boolean isProxyBean(Object bean) {
        return AopUtils.isAopProxy(bean);
    }

    protected abstract Object refer(T reference, Class<?> type);

    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public String[] getAnnotationPackages() {
        return annotationPackages;
    }

    public void setAnnotationPackages(String[] annotationPackages) {
        this.annotationPackages = annotationPackages;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final String temp = environment.getProperty(getKey() + ".annotationPackages", "");
        this.annotationPackages = StringUtils.split(temp, ',');
    }

    abstract protected String getKey();
}
