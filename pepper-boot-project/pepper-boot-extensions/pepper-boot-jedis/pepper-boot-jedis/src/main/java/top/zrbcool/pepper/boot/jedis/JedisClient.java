package top.zrbcool.pepper.boot.jedis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.pepper.metrics.integration.jedis.health.JedisHealthTracker;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPropsHolder;
import redis.clients.jedis.PjedisPool;
import redis.clients.util.Pool;

import java.lang.reflect.Field;
import java.util.Collections;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-24
 */
public class JedisClient extends Jedis {
    private static final Logger log = LoggerFactory.getLogger(JedisClient.class);
    private static final Long RELEASE_SUCCESS = 1L;
    private final String namespace;
    private final PjedisPool jedisPool;
    private final JedisPoolConfig jedisPoolConfig;
    private final String address;
    private final int port;

    PjedisPool getJedisPool() {
        return jedisPool;
    }

    public JedisClient(String namespace, JedisPoolConfig jedisPoolConfig, String address, int port) {
        this.namespace = namespace;
        this.jedisPoolConfig = jedisPoolConfig;
        this.address = address;
        this.port = port;
        JedisPropsHolder.NAMESPACE.set(namespace);
        this.jedisPool = new PjedisPool(jedisPoolConfig, address, port);
        JedisHealthTracker.addJedisPool(namespace, jedisPool);
    }

    @SuppressWarnings("unchecked")
    public void warmUp() {
        Field field;
        try {
            field = Pool.class.getDeclaredField("internalPool");
            field.setAccessible(true);
            GenericObjectPool internalPool = (GenericObjectPool) ReflectionUtils.getField(field, jedisPool);
            if (internalPool != null) {
                internalPool.preparePool();
            }
        } catch (Exception e) {
            log.error("error when warmUp()! ", e);
        }
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

    public boolean tryLock(String lockKey, String requestId, int expireTime) {
        try (Jedis jedis = jedisPool.getResource()){
            String result = jedis.set(lockKey, requestId, "NX", "PX", expireTime);
            if ("OK".equals(result)) {
                return true;
            }
        }
        return false;
    }

    public boolean releaseDistributedLock(String lockKey, String requestId) {
        try (Jedis jedis = jedisPool.getResource()){
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
            if (RELEASE_SUCCESS.equals(result)) {
                return true;
            }
        }
        return false;
    }

    public void doExecute(JedisInternalScripts scripts) {
        try (Jedis jedis = jedisPool.getResource()){
            scripts.script(jedis);
        }
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getNamespace() {
        return namespace;
    }

    public JedisPoolConfig getJedisPoolConfig() {
        return jedisPoolConfig;
    }
}
