package top.zrbcool.pepper.boot.jedis;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import top.zrbcool.pepper.boot.core.AbstractAnnotationBean;

import static top.zrbcool.pepper.boot.jedis.BaseJedisConfiguration.PREFIX_APP_JEDIS;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-25
 */
public class JedisClientAnnotationBean extends AbstractAnnotationBean<JedisClientRefer> implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return super.postProcessBeforeInitialization(bean, beanName, JedisClientRefer.class);
    }

    @Override
    protected Object refer(JedisClientRefer reference, Class<?> type) {
        return beanFactory.getBean(reference.namespace() + JedisClient.class.getSimpleName(), type);
    }

    @Override
    protected String getKey() {
        return PREFIX_APP_JEDIS;
    }

}
