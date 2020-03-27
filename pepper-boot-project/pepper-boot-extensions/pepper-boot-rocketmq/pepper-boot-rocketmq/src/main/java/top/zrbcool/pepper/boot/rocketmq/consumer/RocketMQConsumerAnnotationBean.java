package top.zrbcool.pepper.boot.rocketmq.consumer;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import top.zrbcool.pepper.boot.core.AbstractAnnotationBean;
import top.zrbcool.pepper.boot.rocketmq.Constants;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-25
 */
public class RocketMQConsumerAnnotationBean extends AbstractAnnotationBean<RocketMQConsumerRefer> implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return super.postProcessBeforeInitialization(bean, beanName, RocketMQConsumerRefer.class);
    }

    @Override
    protected Object refer(RocketMQConsumerRefer reference, Class<?> type) {
        return beanFactory.getBean(reference.namespace() + EnhancedDefaultMQPushConsumer.class.getSimpleName(), type);
    }

    @Override
    protected String getKey() {
        return Constants.PREFIX_APP_RMQ_CONSUMER;
    }

}
