package top.zrbcool.pepper.boot.samples.rocketmq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.zrbcool.pepper.boot.rocketmq.producer.EnhancedDefaultMQProducer;
import top.zrbcool.pepper.boot.rocketmq.producer.RocketMQProducerRefer;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhangrongbincool@163.com
 * @version 19-12-31
 */
@Component
public class SampleProducer {
    @RocketMQProducerRefer
    private EnhancedDefaultMQProducer producer;
    private ExecutorService producerExecutor = Executors.newSingleThreadExecutor();
    @PostConstruct
    public void init() {
        producerExecutor.submit(() -> {
            for (int i = 0; i < 10000; i++) {
                try {
                    producer.send("PEPPER-TEST-TOPIC", "just test");
                } catch (InterruptedException | RemotingException | MQClientException | MQBrokerException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
