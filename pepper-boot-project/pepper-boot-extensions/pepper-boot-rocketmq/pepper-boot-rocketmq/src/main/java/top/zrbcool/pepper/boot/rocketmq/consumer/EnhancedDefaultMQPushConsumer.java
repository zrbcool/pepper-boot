package top.zrbcool.pepper.boot.rocketmq.consumer;

import com.pepper.metrics.integration.rocketmq.proxy.MessageListenerConcurrentlyProxy;
import com.pepper.metrics.integration.rocketmq.proxy.MessageListenerOrderlyProxy;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;

/**
 * @author zhangrongbincool@163.com
 * @version 19-12-31
 */
public class EnhancedDefaultMQPushConsumer extends DefaultMQPushConsumer {
    private String namespace;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    private String subExpression = "*";

    public String getSubExpression() {
        return subExpression;
    }

    public void setSubExpression(String subExpression) {
        this.subExpression = subExpression;
    }

    public void subscribe(String topic) throws MQClientException {
        super.subscribe(topic, subExpression);
    }

    @Override
    public void start() throws MQClientException {
        final MessageListener messageListener = getMessageListener();
        if (messageListener instanceof MessageListenerConcurrently) {
            super.setMessageListener(new MessageListenerConcurrentlyProxy(namespace, getConsumerGroup(), (MessageListenerConcurrently) messageListener));
        } else if (messageListener instanceof MessageListenerOrderly) {
            super.setMessageListener(new MessageListenerOrderlyProxy(namespace, getConsumerGroup(), (MessageListenerOrderly) messageListener));
        } else {
            super.setMessageListener(messageListener);
        }
        super.start();
    }
}
