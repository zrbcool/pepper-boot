package top.zrbcool.pepper.boot.asynchttpclient.starter;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import top.zrbcool.pepper.boot.asynchttpclient.EnableHttpClient;
import top.zrbcool.pepper.boot.asynchttpclient.HttpClient;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-11
 */
@Configuration
@ConditionalOnClass(EnableHttpClient.class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnWebApplication
public class HttpClientAutoConfiguration {

    @Bean
    public HealthIndicator httpClientHealthIndicator() {
        return () -> {
            if (HttpClient.readyForRequest()) {
                return Health.up().build();
            } else {
                return Health.down().withDetail("HttpNioClient", "Down").build();
            }
        };
    }
}
