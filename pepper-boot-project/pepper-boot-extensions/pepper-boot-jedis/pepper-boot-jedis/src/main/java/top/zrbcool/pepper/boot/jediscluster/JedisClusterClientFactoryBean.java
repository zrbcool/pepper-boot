package top.zrbcool.pepper.boot.jediscluster;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import redis.clients.jedis.JedisPoolConfig;
import top.zrbcool.pepper.boot.core.CustomizedPropertiesBinder;
import top.zrbcool.pepper.boot.jedis.BaseJedisConfiguration;

/**
 * Created by zhangrongbin on 2018/10/10.
 */
public class JedisClusterClientFactoryBean extends BaseJedisConfiguration
        implements FactoryBean<JedisClusterClient>, EnvironmentAware, BeanNameAware {
    private final Class[] ARGUMENT_TYPES = {String.class, JedisPoolConfig.class, String.class};
    private Environment environment;
    private String beanName;

    @Autowired
    protected CustomizedPropertiesBinder binder;

    @Override
    public JedisClusterClient getObject() throws NoSuchFieldException {
        String namespace = StringUtils.substringBefore(beanName, JedisClusterClient.class.getSimpleName());

        String addressKey = getPreFix() + "." + namespace + ".address";
        String address = environment.getProperty(addressKey);
        Assert.isTrue(StringUtils.isNotEmpty(address), String.format("%s=%s must be not null! ", addressKey, address));

        JedisPoolConfig jedisPoolConfig = createJedisPoolConfig();
        jedisPoolConfig.setTestOnReturn(false);
        Bindable<?> target = Bindable.of(JedisPoolConfig.class).withExistingValue(jedisPoolConfig);
        binder.bind(getPreFix() + "." + namespace + ".pool", target);

        final Object[] arguments = {namespace, jedisPoolConfig, address};

        return JedisClusterClientProxyFactory.getProxy(ARGUMENT_TYPES, arguments);
    }

    protected String getPreFix() {
        return PREFIX_APP_JEDIS_CLUSTER;
    }

    @Override
    public Class<?> getObjectType() {
        return JedisClusterClient.class;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
}
