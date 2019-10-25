package top.zrbcool.pepper.boot.jedis;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import redis.clients.jedis.JedisPoolConfig;
import top.zrbcool.pepper.boot.core.CustomizedPropertiesBinder;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-24
 */
public class JedisClientFactoryBean extends BaseJedisConfiguration
        implements FactoryBean<JedisClient>, EnvironmentAware, BeanNameAware {
    private Environment environment;
    private String beanName;
    private final Class[] ARGUMENT_TYPES = {String.class, JedisPoolConfig.class, String.class, int.class};

    @Autowired
    protected CustomizedPropertiesBinder binder;

    @Override
    public JedisClient getObject() {
        String namespace = StringUtils.substringBefore(beanName, JedisClient.class.getSimpleName());

        String addressKey = getPreFix() + "." + namespace + ".address";
        String address = environment.getProperty(addressKey);
        Assert.isTrue(StringUtils.isNotEmpty(address), String.format("%s=%s must be not null! ", addressKey, address));

        String portKey = getPreFix() + "." + namespace + ".port";
        String port = environment.getProperty(portKey);
        Assert.isTrue(StringUtils.isNotEmpty(port) && NumberUtils.isCreatable(port), String.format("%s=%s must be not null! and must be a number!", portKey, port));

        JedisPoolConfig jedisPoolConfig = createJedisPoolConfig();
        Bindable<?> target = Bindable.of(JedisPoolConfig.class).withExistingValue(jedisPoolConfig);
        binder.bind(getPreFix() + "." + namespace + ".pool", target);

        final Object[] arguments = {namespace, jedisPoolConfig, address, Integer.parseInt(port)};

//        PjedisPool jedisPool = jedisClient.getJedisPool();
//        JedisHealthTracker.addJedisPool(namespace, jedisPool);
        return JedisClientProxyFactory.getProxy(ARGUMENT_TYPES, arguments);
    }

    protected String getPreFix() {
        return PREFIX_APP_JEDIS;
    }

    @Override
    public Class<?> getObjectType() {
        return JedisClient.class;
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
