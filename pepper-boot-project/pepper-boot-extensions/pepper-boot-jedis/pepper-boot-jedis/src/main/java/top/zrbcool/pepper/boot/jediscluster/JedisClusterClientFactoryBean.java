package top.zrbcool.pepper.boot.jediscluster;

import com.pepper.metrics.core.extension.ExtensionLoader;
import com.pepper.metrics.integration.jedis.JedisClusterProxyFactory;
import com.pepper.metrics.integration.jedis.health.JedisClusterHealthTracker;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPropsHolder;
import top.zrbcool.pepper.boot.core.CustomizedPropertiesBinder;
import top.zrbcool.pepper.boot.jedis.BaseJedisConfiguration;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-24
 */
public class JedisClusterClientFactoryBean extends BaseJedisConfiguration
        implements FactoryBean<JedisClusterClient>, EnvironmentAware, BeanNameAware {
    private Environment environment;
    private String beanName;

    @Autowired
    protected CustomizedPropertiesBinder binder;

    @Override
    public JedisClusterClient getObject() {
        String namespace = StringUtils.substringBefore(beanName, JedisClusterClient.class.getSimpleName());

        String addressKey = getPreFix() + "." + namespace + ".address";
        String address = environment.getProperty(addressKey);
        Assert.isTrue(StringUtils.isNotEmpty(address), String.format("%s=%s must be not null! ", addressKey, address));

        JedisPoolConfig jedisPoolConfig = createJedisPoolConfig();
        jedisPoolConfig.setTestOnReturn(false);
        Bindable<?> target = Bindable.of(JedisPoolConfig.class).withExistingValue(jedisPoolConfig);
        binder.bind(getPreFix() + "." + namespace + ".pool", target);

        String[] commonClusterRedisArray = address.split(",");
        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        for (String clusterHostAndPort : commonClusterRedisArray) {
            String host = clusterHostAndPort.split(":")[0].trim();
            int port = Integer.parseInt(clusterHostAndPort.split(":")[1].trim());
            jedisClusterNodes.add(new HostAndPort(host, port));
        }

        JedisPropsHolder.NAMESPACE.set(namespace);
        int defaultConnectTimeout = 2000;
        int defaultConnectMaxAttempts = 20;

        final Class[] argumentTypes = {
                Set.class,
                int.class,
                int.class,
                GenericObjectPoolConfig.class,
                String.class,
                String.class
        };
        final Object[] arguments = {jedisClusterNodes, defaultConnectTimeout, defaultConnectMaxAttempts, jedisPoolConfig, namespace, address};

        final JedisClusterClient jedisClusterClient = ExtensionLoader.getExtensionLoader(JedisClusterProxyFactory.class)
                .getExtension("cglib")
                .getProxy(JedisClusterClient.class, namespace, argumentTypes, arguments);
        JedisClusterHealthTracker.addJedisCluster(namespace, jedisClusterClient);

        return jedisClusterClient;
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
