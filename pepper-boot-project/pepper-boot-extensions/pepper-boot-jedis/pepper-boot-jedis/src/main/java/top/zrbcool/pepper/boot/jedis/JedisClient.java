package top.zrbcool.pepper.boot.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-24
 */
public class JedisClient extends Jedis {
    private static final Long RELEASE_SUCCESS = 1L;
    private final JedisPool jedisPool;

    public JedisClient(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    JedisPool getJedisPool() {
        return jedisPool;
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

    public  boolean releaseDistributedLock(String lockKey, String requestId) {
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

}
