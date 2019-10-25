package top.zrbcool.pepper.boot.jedis;

import org.junit.Test;
import redis.clients.jedis.Jedis;
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

        final Class[] argumentTypes = {String.class, JedisPoolConfig.class, String.class, int.class};
        final Object[] arguments = {"default", config, "192.168.100.221", 6379};

        final JedisClient jedisClient = JedisClientProxyFactory.getProxy(argumentTypes, arguments);
        jedisClient.set("hello", "world");
        System.out.println(jedisClient.get("hello"));
        ResultHolder<String> result = new ResultHolder<>();
        jedisClient.doExecute(jedis -> {
            jedis.set("functional", "functional");
            final String functional = jedis.get("functional");
            result.setResult(functional);
        });

        new JedisInternalScripts() {
            @Override
            public void script(Jedis jedis) {

            }
        };
        System.out.println(result.getResult());
    }


}
