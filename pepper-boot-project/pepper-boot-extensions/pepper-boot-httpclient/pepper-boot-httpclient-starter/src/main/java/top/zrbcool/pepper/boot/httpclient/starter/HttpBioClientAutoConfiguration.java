package top.zrbcool.pepper.boot.httpclient.starter;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import top.zrbcool.pepper.boot.httpclient.EnableHttpBioClient;
import top.zrbcool.pepper.boot.httpclient.HttpBioClient;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-11
 */
@Configuration
@ConditionalOnClass(EnableHttpBioClient.class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnWebApplication
public class HttpBioClientAutoConfiguration {

    @Bean
    public HealthIndicator httpBioClientHealthIndicator() {
        return () -> {
            if (HttpBioClient.readyForRequest()) {
                return Health.up().build();
            } else {
                return Health.down().withDetail("HttpBioClient", "Down").build();
            }
        };
    }
}
