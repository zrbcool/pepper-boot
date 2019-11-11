package top.zrbcool.pepper.boot.motan.starter;

import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.config.springsupport.AnnotationBean;
import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;
import com.weibo.api.motan.util.MotanSwitcherUtil;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import top.zrbcool.pepper.boot.core.Loggers;
import top.zrbcool.pepper.boot.motan.MotanConfigPrintSpringListener;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-7
 */
@ConditionalOnClass({
        MotanService.class,
        MotanReferer.class,
        AnnotationBean.class
})
public class MotanAutoConfiguration {
    private static final Logger logger = Loggers.getFrameworkLogger();

    @Bean
    public AnnotationBean annotationBean() {
        return new AnnotationBean();
    }

    @Bean
    public ApplicationListener<ApplicationEvent> motanSwitcherListener() {
        return event -> {
            if (event instanceof ApplicationReadyEvent) {
                MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
                logger.info("motan service has started!");
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, false);
                        TimeUnit.SECONDS.sleep(1);
                        logger.info("motan service has shutdown gracefully!");
                    } catch (Exception e) {
                        logger.error("error occurred during motan service shutdown! pls check! ", e);
                    }
                }));
                logger.info("motan service shutdown hook added!");
            } else if (event instanceof ContextClosedEvent) {
                logger.info("ContextClosedEvent triggered, will start shutdown motan service...");
            }
        };
    }

    @Bean
    @ConditionalOnClass({
            Logger.class,
            ThreadContext.class
    })
    public MotanConfigPrintSpringListener motanConfigPrintSpringListener() {
        return new MotanConfigPrintSpringListener();
    }

    @Bean
    @ConditionalOnClass({
            HealthIndicator.class,
            Health.class
    })
    public HealthIndicator motanHealthIndicator() {
        return () -> {
            if (MotanSwitcherUtil.isOpen(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER)) {
                return Health.up().build();
            } else {
                return Health.down().withDetail("REGISTRY_HEARTBEAT_SWITCHER", "Down").build();
            }
        };
    }



}
