package top.zrbcool.pepper.boot.jedis;

import redis.clients.jedis.Jedis;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-24
 */
@FunctionalInterface
public interface JedisInternalScripts {
    public void script(Jedis jedis);
}
