package top.zrbcool.pepper.boot.core;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.Map;

/**
 * Created by zhangrongbin on 2018/10/08.
 */
public abstract class AbstractConfigPrintSpringListener<T> implements ApplicationContextAware, ApplicationListener<ApplicationEvent> {
    private static final Logger logger = Loggers.getFrameworkLogger();
    public static final String CONFIG_PREFIX = "** PEPPER BOOT CONFIG - ";
    private ApplicationContext applicationContext;

    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent || event instanceof ApplicationFailedEvent) {

            Map<String, T> beansOfType = applicationContext.getBeansOfType(getConfigClass());
            if (beansOfType != null && beansOfType.size() > 0) {
                logger.info(StringUtils.rightPad(String.format(CONFIG_PREFIX + "%s START (%s) ", getModuleName(), event.getClass().getSimpleName()), 150, '*'));
                for (Map.Entry<String, T> entry : beansOfType.entrySet()) {
                    logConfigInfo(entry);
                }
                logger.info(StringUtils.rightPad(String.format(CONFIG_PREFIX + "%s END (%s) ", getModuleName(), event.getClass().getSimpleName()), 150, '*'));
            } else {
                logger.warn("no {} config found! pls check!!", getModuleName());
            }
        }
    }

    protected void logConfigInfo(Map.Entry<String, T> entry) {
        try {
            logger.info(
                    "{}: {}",
                    entry.getKey(),
                    JSON.toJSONString(entry.getValue(), true)
            );
        } catch (Exception e) {
            try {
                logger.info(
                        "{}-{}",
                        entry.getKey(),
                        ToStringBuilder.reflectionToString(entry.getValue(), ToStringStyle.MULTI_LINE_STYLE)
                );
            } catch (Exception t) {
                logger.error("error when logConfigInfo: ", t);
            }
        }
    }

    protected abstract Class<T> getConfigClass();

    protected abstract String getModuleName();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
