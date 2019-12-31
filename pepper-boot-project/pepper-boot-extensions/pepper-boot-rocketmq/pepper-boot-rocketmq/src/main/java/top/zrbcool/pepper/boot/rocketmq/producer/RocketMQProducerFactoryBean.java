package top.zrbcool.pepper.boot.rocketmq.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import top.zrbcool.pepper.boot.core.CustomizedPropertiesBinder;
import top.zrbcool.pepper.boot.rocketmq.Constants;

/**
 * @author zhangrongbincool@163.com
 * @version 19-12-30
 */
public class RocketMQProducerFactoryBean implements FactoryBean<EnhancedDefaultMQProducer>, EnvironmentAware, BeanNameAware {
    private static final Logger log = LoggerFactory.getLogger(EnhancedDefaultMQProducer.class);
    private Environment environment;
    private String beanName;
    private String producerGroup;
    private String namespace;

    @Autowired
    protected CustomizedPropertiesBinder binder;

    @Override
    public EnhancedDefaultMQProducer getObject() throws MQClientException {
        /*
         * caf.mq.rocketmq.producer.compressMsgBodyOverHowmuch = 4096
         * caf.mq.rocketmq.producer.defaultTopicQueueNums = 4
         * caf.mq.rocketmq.producer.heartbeatBrokerInterval = 30000
         * caf.mq.rocketmq.producer.maxMessageSize = 4194304
         * caf.mq.rocketmq.producer.persistConsumerOffsetInterval = 5000
         * caf.mq.rocketmq.producer.pollNameServerInterval = 30000
         * caf.mq.rocketmq.producer.retryAnotherBrokerWhenNotStoreOK = false
         * caf.mq.rocketmq.producer.retryTimesWhenSendAsyncFailed = 2
         * caf.mq.rocketmq.producer.retryTimesWhenSendFailed = 2
         * caf.mq.rocketmq.producer.sendLatencyFaultEnable = false
         * caf.mq.rocketmq.producer.sendMessageWithVIPChannel = true
         * caf.mq.rocketmq.producer.sendMsgTimeout = 3000
         * caf.mq.rocketmq.producer.vipChannelEnabled = true
         */

        EnhancedDefaultMQProducer defaultMQProducer = new EnhancedDefaultMQProducer();
        defaultMQProducer.setNamespace(namespace);
        defaultMQProducer.setProducerGroup(producerGroup);
        Bindable<?> target = Bindable.of(EnhancedDefaultMQProducer.class).withExistingValue(defaultMQProducer);
        // bind default params
        binder.bind(getPreFix(), target);
        binder.bind(getPreFix() + "." + namespace, target);
        /*
         * TODO:
         * defaultMQProducer.setClientCallbackExecutorThreads(producerConfig.getClientCallbackExecutorThreads());\
         * defaultMQProducer.setUnitMode(producerConfig.isUnitMode());
         */
        try {
            defaultMQProducer.start();
        } catch (MQClientException e) {
            log.error("", e);
            throw e;
        }
        return defaultMQProducer;
    }

    protected String getPreFix() {
        return Constants.PREFIX_APP_RMQ_PRODUCER;
    }

    @Override
    public Class<?> getObjectType() {
        return EnhancedDefaultMQProducer.class;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
