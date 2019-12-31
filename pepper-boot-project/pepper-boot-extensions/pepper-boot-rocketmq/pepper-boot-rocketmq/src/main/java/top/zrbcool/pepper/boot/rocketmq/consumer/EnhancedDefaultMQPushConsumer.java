package top.zrbcool.pepper.boot.rocketmq.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
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

    @Override
    public void setMessageListener(MessageListener messageListener) {
        super.setMessageListener(messageListener);
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
}
