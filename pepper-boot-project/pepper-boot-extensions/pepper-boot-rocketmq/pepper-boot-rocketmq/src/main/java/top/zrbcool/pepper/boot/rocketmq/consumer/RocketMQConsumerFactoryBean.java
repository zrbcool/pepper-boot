package top.zrbcool.pepper.boot.rocketmq.consumer;

import com.pepper.metrics.integration.rocketmq.RocketMQHealthTracker;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.protocol.body.ConsumerRunningInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import top.zrbcool.pepper.boot.core.CustomizedPropertiesBinder;
import top.zrbcool.pepper.boot.rocketmq.Constants;
import top.zrbcool.pepper.boot.rocketmq.producer.EnhancedDefaultMQProducer;

/**
 * @author zhangrongbincool@163.com
 * @version 19-12-30
 */
public class RocketMQConsumerFactoryBean implements FactoryBean<EnhancedDefaultMQPushConsumer>, DisposableBean, BeanNameAware {
    private static final Logger log = LoggerFactory.getLogger(EnhancedDefaultMQPushConsumer.class);
    private String namespace;
    private EnhancedDefaultMQPushConsumer defaultMQPushConsumer;

    @Autowired
    protected CustomizedPropertiesBinder binder;
    private String beanName;

    @Override
    public EnhancedDefaultMQPushConsumer getObject() {
        /*
         * caf.mq.rocketmq.consumer.subExpression = *
         * caf.mq.rocketmq.consumer.adjustThreadPoolNumsThreshold = 100000
         * caf.mq.rocketmq.consumer.consumeConcurrentlyMaxSpan = 2000
         * caf.mq.rocketmq.consumer.consumeMessageBatchMaxSize = 1
         * caf.mq.rocketmq.consumer.consumeThreadMax = 64
         * caf.mq.rocketmq.consumer.consumeThreadMin = 20
         * caf.mq.rocketmq.consumer.consumeTimeout = 15
         * caf.mq.rocketmq.consumer.heartbeatBrokerInterval = 30000
         * caf.mq.rocketmq.consumer.persistConsumerOffsetInterval = 5000
         * caf.mq.rocketmq.consumer.pollNameServerInterval = 30000
         * caf.mq.rocketmq.consumer.postSubscriptionWhenPull = false
         * caf.mq.rocketmq.consumer.pullBatchSize = 32
         * caf.mq.rocketmq.consumer.pullInterval = 0
         * caf.mq.rocketmq.consumer.pullThresholdForQueue = 1000
         * caf.mq.rocketmq.consumer.pullThresholdForTopic = -1
         * caf.mq.rocketmq.consumer.pullThresholdSizeForQueue = 100
         * caf.mq.rocketmq.consumer.pullThresholdSizeForTopic = -1
         * caf.mq.rocketmq.consumer.suspendCurrentQueueTimeMillis = 1000
         * caf.mq.rocketmq.consumer.vipChannelEnabled = true
         *
         */
        defaultMQPushConsumer = new EnhancedDefaultMQPushConsumer();
        defaultMQPushConsumer.setNamespace(namespace);
        // bind default params
        Bindable<?> target = Bindable.of(DefaultMQPushConsumer.class).withExistingValue(defaultMQPushConsumer);
        binder.bind(getPreFix(), target);
        binder.bind(getPreFix() + "." + namespace, target);
        RocketMQHealthTracker.addDefaultMQPushConsumer(namespace, defaultMQPushConsumer);
        return defaultMQPushConsumer;
    }

    protected String getPreFix() {
        return Constants.PREFIX_APP_RMQ_CONSUMER;
    }

    @Override
    public Class<?> getObjectType() {
        return EnhancedDefaultMQProducer.class;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public void destroy() {
        try {
            defaultMQPushConsumer.shutdown();
            log.info("rocketmq - [{}] consumer has shutdown gracefully!", namespace);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
}
