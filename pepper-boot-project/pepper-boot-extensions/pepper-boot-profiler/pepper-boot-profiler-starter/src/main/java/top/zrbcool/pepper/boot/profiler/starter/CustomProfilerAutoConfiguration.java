package top.zrbcool.pepper.boot.profiler.starter;

import com.pepper.metrics.integration.custom.CustomProfilerAspect;
import org.springframework.aop.aspectj.AspectJAroundAdvice;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-12
 */
@Configuration
@ConditionalOnClass({
        AspectJAroundAdvice.class,
        CustomProfilerAspect.class
})
public class CustomProfilerAutoConfiguration {
    @Bean
    public CustomProfilerAspect customProfilerAspect() {
        return new CustomProfilerAspect();
    }
}
