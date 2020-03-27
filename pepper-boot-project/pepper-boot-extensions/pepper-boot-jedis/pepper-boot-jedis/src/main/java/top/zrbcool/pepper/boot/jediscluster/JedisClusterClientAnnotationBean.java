package top.zrbcool.pepper.boot.jediscluster;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import top.zrbcool.pepper.boot.core.AbstractAnnotationBean;
import top.zrbcool.pepper.boot.jedis.JedisClient;

import static top.zrbcool.pepper.boot.jedis.BaseJedisConfiguration.PREFIX_APP_JEDIS_CLUSTER;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-25
 */
public class JedisClusterClientAnnotationBean extends AbstractAnnotationBean<JedisClusterClientRefer> implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return super.postProcessBeforeInitialization(bean, beanName, JedisClusterClientRefer.class);
    }

    @Override
    protected Object refer(JedisClusterClientRefer reference, Class<?> type) {
        return beanFactory.getBean(reference.namespace() + JedisClusterClient.class.getSimpleName(), type);
    }

    @Override
    protected String getKey() {
        return PREFIX_APP_JEDIS_CLUSTER;
    }

}
