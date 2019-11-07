package top.zrbcool.pepper.boot.motan;

import com.weibo.api.motan.config.springsupport.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import top.zrbcool.pepper.boot.core.CustomizedPropertiesBinder;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-7
 */
public class MotanBeanPostProcessor extends BaseMotanConfiguration implements BeanPostProcessor, Ordered, EnvironmentAware, BeanFactoryAware {

    @Autowired
    protected CustomizedPropertiesBinder binder;
    private Environment environment;
    private BeanFactory beanFactory;

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof AnnotationBean) {
//            String namespace = StringUtils.substringBefore(beanName, AnnotationBean.class.getSimpleName());
//
//            Bindable<?> target = Bindable.of(AnnotationBean.class).withExistingValue((AnnotationBean) bean);
//            binder.bind(PREFIX_APP_MOTAN + "." + namespace + ".annotation", target);

        } else if (bean instanceof RegistryConfigBean) {
            String sourceName = StringUtils.substringBefore(beanName, RegistryConfigBean.class.getSimpleName());

            RegistryConfigBean registryConfigBean = (RegistryConfigBean) bean;
            initRegistryConfig(registryConfigBean);

            String globalPrefix = PREFIX_APP_MOTAN + ".registry";
            String nsPrefix = PREFIX_APP_MOTAN + "." + sourceName + ".registry";

            if (StringUtils.isAllEmpty(environment.getProperty(globalPrefix + ".address"),
                    environment.getProperty(nsPrefix + ".address"))) {
                throw new IllegalArgumentException(String.format("%s or %s can not be null!!", globalPrefix, nsPrefix));
            }

            if (StringUtils.isNotEmpty(environment.getProperty(globalPrefix + ".address"))){
                Bindable<?> target = Bindable.of(RegistryConfigBean.class).withExistingValue(registryConfigBean);
                binder.bind(globalPrefix, target);
            }

            Bindable<?> target = Bindable.of(RegistryConfigBean.class).withExistingValue(registryConfigBean);
            binder.bind(nsPrefix, target);

        } else if (bean instanceof ProtocolConfigBean) {
            String sourceName = StringUtils.substringBefore(beanName, ProtocolConfigBean.class.getSimpleName());

            ProtocolConfigBean protocolConfigBean = (ProtocolConfigBean) bean;
            initProtocolConfig(protocolConfigBean);

            Bindable<?> target = Bindable.of(ProtocolConfigBean.class).withExistingValue(protocolConfigBean);
            binder.bind(PREFIX_APP_MOTAN + "." + sourceName + ".protocol", target);
            protocolConfigBean.setBeanName(beanName);

        } else if (bean instanceof BasicServiceConfigBean) {
            String sourceName = StringUtils.substringBefore(beanName, BasicServiceConfigBean.class.getSimpleName());

            String registryBeanName = sourceName + RegistryConfigBean.class.getSimpleName();
            RegistryConfigBean registryConfigBean =
                    beanFactory.getBean(registryBeanName, RegistryConfigBean.class);
            Assert.notNull(registryConfigBean, String.format("%s does not existed in spring context, pls check!", registryBeanName));

            String protocolBeanName = sourceName + ProtocolConfigBean.class.getSimpleName();
            ProtocolConfigBean protocolConfigBean = beanFactory.getBean(protocolBeanName, ProtocolConfigBean.class);
            Assert.notNull(protocolConfigBean, String.format("%s does not existed in spring context, pls check!", protocolBeanName));

            String portKey = PREFIX_APP_MOTAN + "." + sourceName + ".port";
            String port = environment.getProperty(portKey);
            if (StringUtils.isEmpty(port)) {
                port = "10010";
            }
            Assert.isTrue(StringUtils.isNotEmpty(port) && NumberUtils.isCreatable(port), String.format("%s=%s must be not null! and must be a number!", portKey, port));

            BasicServiceConfigBean basicServiceConfigBean = (BasicServiceConfigBean) bean;
            initBasicServiceConfig(registryConfigBean, protocolConfigBean, Integer.parseInt(port), basicServiceConfigBean);

            Bindable<?> target = Bindable.of(BasicServiceConfigBean.class).withExistingValue(basicServiceConfigBean);
            binder.bind(PREFIX_APP_MOTAN + "." + sourceName + ".basic-service", target);

        } else if (bean instanceof BasicRefererConfigBean) {
            String sourceName = StringUtils.substringBefore(beanName, BasicRefererConfigBean.class.getSimpleName());

            String registryBeanName = sourceName + RegistryConfigBean.class.getSimpleName();
            RegistryConfigBean registryConfigBean =
                    beanFactory.getBean(registryBeanName, RegistryConfigBean.class);
            Assert.notNull(registryConfigBean, String.format("%s does not existed in spring context, pls check!", registryBeanName));

            String protocolBeanName = sourceName + ProtocolConfigBean.class.getSimpleName();
            ProtocolConfigBean protocolConfigBean = beanFactory.getBean(protocolBeanName, ProtocolConfigBean.class);
            Assert.notNull(protocolConfigBean, String.format("%s does not existed in spring context, pls check!", protocolBeanName));

            BasicRefererConfigBean basicRefererConfigBean = (BasicRefererConfigBean) bean;
            initBasicRefererConfig(registryConfigBean, protocolConfigBean, basicRefererConfigBean);

            Bindable<?> target = Bindable.of(BasicRefererConfigBean.class).withExistingValue(basicRefererConfigBean);
            binder.bind(PREFIX_APP_MOTAN + "." + sourceName + ".basic-referer", target);

        }

        return bean;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
