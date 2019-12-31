package top.zrbcool.pepper.boot.rocketmq.producer;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;

/**
 * @author zhangrongbincool@163.com
 * @version 19-12-31
 */

public class EnhancedDefaultMQProducer extends DefaultMQProducer {
    private static final Logger log = LoggerFactory.getLogger(EnhancedDefaultMQProducer.class);
    private String namespace;

    public SendResult send(String topic, String tags, String message) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Assert.isTrue(StringUtils.isNotEmpty(message), "message can not be empty!");
        Assert.isTrue(StringUtils.isNotEmpty(tags), "tags can not be empty!");
        Message msg = getMessage(topic, tags, message);
        return super.send(msg);
    }

    public SendResult send(String topic, String message) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Assert.isTrue(StringUtils.isNotEmpty(message), "message can not be empty!");
        Message msg = getMessage(topic, message);
        return super.send(msg);
    }

    private Message getMessage(String topic, String message) {
        Message msg = null;
        try {
            msg = new Message(topic, message.getBytes(RemotingHelper.DEFAULT_CHARSET));
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
        }
        return msg;
    }

    private Message getMessage(String topic, String tags, String message) {
        Message msg = null;
        try {
            msg = new Message(topic, tags, message.getBytes(RemotingHelper.DEFAULT_CHARSET));
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
        }
        return msg;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
