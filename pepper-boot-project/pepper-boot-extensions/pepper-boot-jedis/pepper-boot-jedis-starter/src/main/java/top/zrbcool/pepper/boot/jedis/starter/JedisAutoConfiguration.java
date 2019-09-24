package top.zrbcool.pepper.boot.jedis.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

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
    /**
     * it just a sample!!!~
     */
    @Bean
    public Jedis sampleJedis() {
        return new Jedis("192.168.100.221", 6379);
    }
}
