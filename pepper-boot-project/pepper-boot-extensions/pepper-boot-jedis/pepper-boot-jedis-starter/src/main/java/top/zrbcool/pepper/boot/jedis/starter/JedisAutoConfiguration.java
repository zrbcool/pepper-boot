package top.zrbcool.pepper.boot.jedis.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import top.zrbcool.pepper.boot.jedis.CustomizedConfigurationPropertiesBinder;
import top.zrbcool.pepper.boot.jedis.JedisAnnotationBean;

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
    @Bean
    public CustomizedConfigurationPropertiesBinder customizedConfigurationPropertiesBinder() {
        return new CustomizedConfigurationPropertiesBinder();
    }

    @Bean
    public JedisAnnotationBean jedisAnnotationBean() {
        return new JedisAnnotationBean();
    }
}
