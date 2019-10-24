package top.zrbcool.pepper.boot.jedis;

import org.junit.Test;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-24
 */
public class TestJedisClient {
    @Test
    public void test() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(300);
        config.setMaxIdle(10);
        config.setMinIdle(5);
        config.setMaxWaitMillis(6000);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
        config.setTestWhileIdle(true);
        config.setTestOnCreate(false);
        JedisPool jedisPool = new JedisPool(config, "192.168.100.221", 6379);

        final Class[] argumentTypes = {JedisPool.class};
        final Object[] arguments = {jedisPool};
//        Enhancer enhancer = new Enhancer();
//        enhancer.setSuperclass(JedisClient.class);
//        enhancer.setCallback(new JedisClientMethodInterceptor());
//        final JedisClient jedisClient = (JedisClient) enhancer.create(argumentTypes, arguments);

        final JedisClient jedisClient = JedisClientProxyFactory.getProxy(argumentTypes, arguments);
        jedisClient.set("hello", "world");
        System.out.println(jedisClient.get("hello"));
        ResultHolder<String> result = new ResultHolder<>();
        jedisClient.doExecute(jedis -> {
            jedis.set("functional", "functional");
            final String functional = jedis.get("functional");
            result.setResult(functional);
        });
        System.out.println(result.getResult());
    }
}
