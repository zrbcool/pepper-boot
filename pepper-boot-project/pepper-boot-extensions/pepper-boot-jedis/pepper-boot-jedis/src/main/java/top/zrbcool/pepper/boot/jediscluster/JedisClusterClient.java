package top.zrbcool.pepper.boot.jediscluster;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import redis.clients.jedis.*;
import redis.clients.util.JedisClusterCRC16;
import redis.clients.util.Pool;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-1
 */
public class JedisClusterClient extends JedisCluster {
    private static final Logger log = LoggerFactory.getLogger(JedisClusterClient.class);
    private final String namespace;
    private final String address;
    private final GenericObjectPoolConfig poolConfig;
    private final Map<String, JedisPool> nodes;
    private final int connectTimeout;
    private final int connectMaxAttemps;

    public JedisClusterClient(Set<HostAndPort> jedisClusterNode, int timeout, int maxAttempts, GenericObjectPoolConfig poolConfig, String namespace, String address) throws NoSuchFieldException {
        super(jedisClusterNode, timeout, maxAttempts, poolConfig);
        this.namespace = namespace;
        this.address = address;
        this.poolConfig = poolConfig;
        this.connectMaxAttemps = maxAttempts;
        this.connectTimeout = timeout;
        Field cacheFiled = JedisClusterConnectionHandler.class.getDeclaredField("cache");
        cacheFiled.setAccessible(true);
        JedisClusterInfoCache cache = (JedisClusterInfoCache) ReflectionUtils.getField(cacheFiled, connectionHandler);
        assert cache != null;
        nodes = cache.getNodes();
    }

//    public JedisClusterClient(String namespace, JedisPoolConfig jedisPoolConfig, String address) {
//        this.namespace = namespace;
//        this.jedisPoolConfig = jedisPoolConfig;
//        this.address = address;
//
//        String[] commonClusterRedisArray = address.split(",");
//        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
//        for (String clusterHostAndPort : commonClusterRedisArray) {
//            String host = clusterHostAndPort.split(":")[0].trim();
//            int port = Integer.parseInt(clusterHostAndPort.split(":")[1].trim());
//            jedisClusterNodes.add(new HostAndPort(host, port));
//        }
//
//        JedisPropsHolder.NAMESPACE.set(namespace);
//        int defaultConnectTimeout = 2000;
//        int defaultConnectMaxAttempts = 20;
//        this.internalJedisCluster = PjedisClusterFactory.newJedisCluster(jedisClusterNodes, defaultConnectTimeout, defaultConnectMaxAttempts, jedisPoolConfig);
//
//    }

    public void warmUp() {
        try {
            Field field = Pool.class.getDeclaredField("internalPool");
            field.setAccessible(true);
            for (JedisPool jedisPool : nodes.values()) {
                GenericObjectPool internalPool = (GenericObjectPool) ReflectionUtils.getField(field, jedisPool);
                if (internalPool != null) {
                    internalPool.preparePool();
                }
            }
        } catch (Exception e) {
            log.error("error when warmUp()! ", e);
        }
    }

    public Jedis getResourceByHashTag(Object hashTag) {
        if (hashTag == null || StringUtils.isEmpty(hashTag.toString())) {
            throw new IllegalArgumentException("hashTag can not be null or empty!!!");
        }
        return getResource("{" + hashTag + "}");
    }

    public Jedis getResource(String hashKey) {
        int slot = JedisClusterCRC16.getSlot(hashKey);
        return ((JedisSlotBasedConnectionHandler) connectionHandler).getConnectionFromSlot(slot);
    }

    public boolean setCache(String key, Object obj) {
        return StringUtils.equals("OK", set(key, JSON.toJSONString(obj)));
    }

    public boolean setCache(String key, Object obj, int timeout) {
        return StringUtils.equals("OK", setex(key, timeout, JSON.toJSONString(obj)));
    }

    public <T> T getCache(String key, TypeReference<T> typeReference) {
        String value = get(key);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return JSON.parseObject(value, typeReference);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getAddress() {
        return address;
    }

    public GenericObjectPoolConfig getPoolConfig() {
        return poolConfig;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getConnectMaxAttemps() {
        return connectMaxAttemps;
    }
}
