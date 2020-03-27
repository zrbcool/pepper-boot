package top.zrbcool.pepper.boot.motan;

import com.google.common.collect.Maps;
import com.weibo.api.motan.config.ServiceConfig;
import com.weibo.api.motan.config.springsupport.BasicRefererConfigBean;
import com.weibo.api.motan.config.springsupport.BasicServiceConfigBean;
import com.weibo.api.motan.config.springsupport.ProtocolConfigBean;
import com.weibo.api.motan.config.springsupport.RegistryConfigBean;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.ReflectionUtils;
import top.zrbcool.pepper.boot.core.Loggers;
import top.zrbcool.pepper.boot.util.Log4j2Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-7
 */
public class MotanConfigPrintSpringListener implements
        ApplicationListener<ApplicationEvent>, ApplicationContextAware {
    private static final String MODULE_NAME = "Motan";
    private static final Logger logger = Loggers.getFrameworkLogger();
    private ApplicationContext applicationContext;
    private Map<String, Map<String, Object>> configBeanMap = Maps.newConcurrentMap();

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        final String yyyyMMddHHmmss = DateTime.now().toString("yyyyMMddHHmmss");
        if (event instanceof ApplicationReadyEvent || event instanceof ApplicationFailedEvent) {
            //fillConfigBeanMap(AnnotationBean.class);
            fillConfigBeanMap(RegistryConfigBean.class);
            fillConfigBeanMap(ProtocolConfigBean.class);
            fillConfigBeanMap(BasicServiceConfigBean.class);
            fillConfigBeanMap(BasicRefererConfigBean.class);

            Log4j2Utils.putContextColumn1("config");
            Log4j2Utils.putContextColumn2("motan:" + yyyyMMddHHmmss);
            logger.info(StringUtils.rightPad(String.format("** PEPPER BOOT CONFIG - %s START (%s) ", MODULE_NAME, event.getClass().getSimpleName()), 150, '*'));
            if (MapUtils.isNotEmpty(configBeanMap)) {
                logger.info(Log4j2Utils.LINE);
                List<String> sorted = configBeanMap.keySet().stream().sorted().collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(sorted)) {
                    for (String namespace : sorted) {
                        Log4j2Utils.putContextColumn2("motan:" + namespace + ":" + yyyyMMddHHmmss);
                        process(configBeanMap.get(namespace));
//                        logger.info(
//                                "{}: {}",
//                                namespace,
//                                JSON.toJSONString(
//                                        configBeanMap.get(namespace),
//                                        SerializerFeature.PrettyFormat,
//                                        SerializerFeature.WriteClassName)
//                        );
                    }
                }
                Log4j2Utils.putContextColumn2("motan:" + yyyyMMddHHmmss);
                if (CollectionUtils.isNotEmpty(ServiceConfig.getExistingServices())) {
                    logger.info("existingServices: ");
                    for (String existingService : ServiceConfig.getExistingServices()) {
                        logger.info("{}- {}", "     ", existingService);
                    }
                }
            } else {
                logger.warn("no {} config found! pls check!!", MODULE_NAME);
            }
            logger.info(StringUtils.rightPad(String.format("** PEPPER BOOT - %s END (%s) ", MODULE_NAME, event.getClass().getSimpleName()), 150, '*'));
            Log4j2Utils.clearContext();
        }
    }

    private void process(Map<String, Object> stringObjectMap) {
        if (MapUtils.isNotEmpty(stringObjectMap)) {
            for (Map.Entry<String, Object> entry : stringObjectMap.entrySet()) {
                logger.info("{} - {}", "beanName", entry.getKey());
                Class clazz = entry.getValue().getClass();
                for (Field field : findAllField(clazz)) {
                    if (isStaticFinal(field) || "beanFactory".equalsIgnoreCase(field.getName())) {
                        continue;
                    }
                    ReflectionUtils.makeAccessible(field);
                    Object fieldValue = ReflectionUtils.getField(field, entry.getValue());
                    if (null != fieldValue) {
                        logger.info("\t{} - {}", field.getName(), fieldValue);
                    }
                }
                logger.info(Log4j2Utils.LINE);
            }
        }
    }

    public boolean isStaticFinal(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
    }

    private synchronized <T> void fillConfigBeanMap(Class<T> tClass) {
        Map<String, T> beansOfType = applicationContext.getBeansOfType(tClass);
        if (MapUtils.isNotEmpty(beansOfType)) {
            beansOfType.forEach((key, value) -> {
                String namespace = StringUtils.substringBefore(key, value.getClass().getSimpleName());
                if (!configBeanMap.containsKey(namespace)) {
                    configBeanMap.put(namespace, Maps.newConcurrentMap());
                }
                configBeanMap.get(namespace).put(key, value);
            });
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private List<Field> findAllField(Class clazz) {
        final List<Field> res = new LinkedList<>();
        ReflectionUtils.doWithFields(clazz, res::add);
        return res;
    }
}
