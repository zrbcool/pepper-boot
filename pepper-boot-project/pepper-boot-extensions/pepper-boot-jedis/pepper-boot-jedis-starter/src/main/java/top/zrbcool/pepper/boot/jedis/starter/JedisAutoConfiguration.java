package top.zrbcool.pepper.boot.jedis.starter;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import top.zrbcool.pepper.boot.core.AbstractConfigPrintSpringListener;
import top.zrbcool.pepper.boot.core.Loggers;
import top.zrbcool.pepper.boot.jedis.JedisClient;
import top.zrbcool.pepper.boot.jedis.JedisClientAnnotationBean;
import top.zrbcool.pepper.boot.jediscluster.JedisClusterClient;
import top.zrbcool.pepper.boot.jediscluster.JedisClusterClientAnnotationBean;
import top.zrbcool.pepper.boot.util.Log4j2Utils;

import java.util.Map;

import static top.zrbcool.pepper.boot.core.AbstractConfigPrintSpringListener.CONFIG_PREFIX;

/**
 * @author zhangrongbincool@163.com
 * @version 19-9-24
 */
@ConditionalOnClass({
        Jedis.class,
        JedisPool.class,
        JedisPoolConfig.class
})
public class JedisAutoConfiguration {
    private static final Logger logger = Loggers.getFrameworkLogger();

    @Bean
    public JedisClientAnnotationBean jedisAnnotationBean() {
        return new JedisClientAnnotationBean();
    }

    @Bean
    public JedisClusterClientAnnotationBean jedisClusterClientAnnotationBean() {
        return new JedisClusterClientAnnotationBean();
    }

    @Bean
    @ConditionalOnClass({
            Logger.class,
            ThreadContext.class
    })
    public ApplicationListener<ApplicationEvent> jedisConfigListener() {
        return new AbstractConfigPrintSpringListener<JedisClient>() {
            @Override
            protected Class<JedisClient> getConfigClass() {
                return JedisClient.class;
            }

            @Override
            protected String getModuleName() {
                return "Jedis Standalone";
            }

            @Override
            protected void logConfigInfo(Map.Entry<String, JedisClient> entry) {
                if (entry == null)
                    return;
                JedisClient jedisClient = entry.getValue();

                JedisPoolConfig poolConfig = jedisClient.getJedisPoolConfig();
                Log4j2Utils.putContextColumn1("config");
                Log4j2Utils.putContextColumn2("jedis-std:" +
                        StringUtils.substringBefore(entry.getKey(), JedisClient.class.getSimpleName()) +
                        ":" + DateTime.now().toString("yyyyMMddHHmmss"));
                logger.info("{} {} - {}", CONFIG_PREFIX, "beanName", entry.getKey());
                logger.info("{}\t{} - {}", CONFIG_PREFIX, "mode", "standalone");
                logger.info("{}\t{} - {}", CONFIG_PREFIX, "jedisPoolConfig@type", poolConfig.getClass());
                logger.info("{}\t{} - {}", CONFIG_PREFIX, "address", jedisClient.getAddress());
                logger.info("{}\t{} - {}", CONFIG_PREFIX, "port", jedisClient.getPort());
                logJedisPoolConfigProps(poolConfig);
                logger.info(Log4j2Utils.LINE);
                Log4j2Utils.clearContext();
            }
        };
    }


    @Bean
    @ConditionalOnClass({
            Logger.class,
            ThreadContext.class
    })
    public ApplicationListener<ApplicationEvent> jedisClusterConfigListener() {
        return new AbstractConfigPrintSpringListener<JedisClusterClient>() {
            @Override
            protected Class<JedisClusterClient> getConfigClass() {
                return JedisClusterClient.class;
            }

            @Override
            protected String getModuleName() {
                return "Jedis Cluster";
            }

            @Override
            protected void logConfigInfo(Map.Entry<String, JedisClusterClient> entry) {
                if (entry == null)
                    return;
                JedisClusterClient clusterClient = entry.getValue();
                JedisPoolConfig poolConfig = (JedisPoolConfig) clusterClient.getPoolConfig();
                Log4j2Utils.putContextColumn1("config");
                Log4j2Utils.putContextColumn2("jedis-cluster:" +
                        StringUtils.substringBefore(entry.getKey(), JedisClusterClient.class.getSimpleName()) +
                        ":" + DateTime.now().toString("yyyyMMddHHmmss"));
                logger.info("{} {} - {}", CONFIG_PREFIX, "beanName", entry.getKey());
                logger.info("{}\t{} - {}", CONFIG_PREFIX, "mode", "cluster");
                logger.info("{}\t{} - {}", CONFIG_PREFIX, "jedisPoolConfig@type", poolConfig.getClass());
                logger.info("{}\t{} - {}", CONFIG_PREFIX, "address", clusterClient.getAddress());
                logger.info("{}\t{} - {}", CONFIG_PREFIX, "defaultConnectMaxAttempts", clusterClient.getConnectMaxAttempts());
                logger.info("{}\t{} - {}", CONFIG_PREFIX, "defaultConnectTimeout", clusterClient.getConnectTimeout());
                logJedisPoolConfigProps(poolConfig);
                logger.info(Log4j2Utils.LINE);
                Log4j2Utils.clearContext();
            }
        };
    }

    private void logJedisPoolConfigProps(JedisPoolConfig poolConfig) {
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "maxIdle", poolConfig.getMaxIdle());
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "maxTotal", poolConfig.getMaxTotal());
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "minIdle", poolConfig.getMinIdle());
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "maxWaitMillis", poolConfig.getMaxWaitMillis());
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "minEvictableIdleTimeMillis", poolConfig.getMinEvictableIdleTimeMillis());
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "numTestsPerEvictionRun", poolConfig.getNumTestsPerEvictionRun());
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "softMinEvictableIdleTimeMillis", poolConfig.getSoftMinEvictableIdleTimeMillis());
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "timeBetweenEvictionRunsMillis", poolConfig.getTimeBetweenEvictionRunsMillis());
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "blockWhenExhausted", poolConfig.getBlockWhenExhausted());
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "evictionPolicyClassName", poolConfig.getEvictionPolicyClassName());
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "fairness", poolConfig.getFairness());
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "jmxEnabled", poolConfig.getJmxEnabled());
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "jmxNameBase", poolConfig.getJmxNameBase());
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "jmxNamePrefix", poolConfig.getJmxNamePrefix());
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "lifo", poolConfig.getLifo());
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "testOnBorrow", poolConfig.getTestOnBorrow());
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "testOnCreate", poolConfig.getTestOnCreate());
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "testOnReturn", poolConfig.getTestOnReturn());
        logger.info("{}\t{} - {}", CONFIG_PREFIX, "testWhileIdle", poolConfig.getTestWhileIdle());
    }

}
