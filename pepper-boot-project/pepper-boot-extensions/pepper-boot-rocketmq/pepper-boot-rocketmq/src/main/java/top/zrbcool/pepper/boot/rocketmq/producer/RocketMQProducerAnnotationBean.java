package top.zrbcool.pepper.boot.rocketmq.producer;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import top.zrbcool.pepper.boot.core.AbstractAnnotationBean;
import top.zrbcool.pepper.boot.rocketmq.Constants;
import top.zrbcool.pepper.boot.rocketmq.consumer.EnhancedDefaultMQPushConsumer;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-25
 */
public class RocketMQProducerAnnotationBean extends AbstractAnnotationBean<RocketMQProducerRefer> implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return super.postProcessBeforeInitialization(bean, beanName, RocketMQProducerRefer.class);
    }

    @Override
    protected Object refer(RocketMQProducerRefer reference, Class<?> type) {
        return beanFactory.getBean(reference.namespace() + EnhancedDefaultMQProducer.class.getSimpleName(), type);
    }

    @Override
    protected String getKey() {
        return Constants.PREFIX_APP_RMQ_PRODUCER;
    }

}
