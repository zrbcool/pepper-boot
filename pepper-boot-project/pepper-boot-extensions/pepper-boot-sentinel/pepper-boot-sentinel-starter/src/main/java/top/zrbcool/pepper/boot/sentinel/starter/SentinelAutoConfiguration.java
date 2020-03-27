package top.zrbcool.pepper.boot.sentinel.starter;

import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.zrbcool.pepper.boot.sentinel.SentinelHttpFilter;
import top.zrbcool.pepper.boot.sentinel.SentinelMetricsAdapter;

import javax.servlet.Filter;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-12
 */
@Configuration
@ConditionalOnClass({
        SphU.class
})
public class SentinelAutoConfiguration {
    @Bean
    public SentinelMetricsAdapter sentinelMetricsAdapter() {
        SentinelMetricsAdapter adapter = new SentinelMetricsAdapter();
        adapter.init();
        return adapter;
    }

    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    @Bean
    public FilterRegistrationBean sentinelFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SentinelHttpFilter());
        registration.addUrlPatterns("/*");
        registration.setName("sentinelHttpFilter");
        registration.setOrder(1);

        return registration;
    }
}
