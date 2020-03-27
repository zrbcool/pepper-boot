package top.zrbcool.pepper.boot.samples.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.zrbcool.pepper.boot.rocketmq.consumer.EnableRocketMQConsumer;
import top.zrbcool.pepper.boot.rocketmq.producer.EnableRocketMQProducer;

/**
 * @author zhangrongbincool@163.com
 * @version 19-9-24
 */
@Slf4j
@EnableRocketMQConsumer
@EnableRocketMQProducer(producerGroup = "RocketMQSampleApplication")
@SpringBootApplication
public class RocketMQSampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(RocketMQSampleApplication.class);
    }

}
