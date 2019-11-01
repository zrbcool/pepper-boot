package top.zrbcool.pepper.boot.jediscluster;

import net.sf.cglib.proxy.Enhancer;
import top.zrbcool.pepper.boot.jedis.JedisClient;
import top.zrbcool.pepper.boot.jedis.JedisClientMethodInterceptor;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-24
 */
public class JedisClusterClientProxyFactory {
    public static JedisClusterClient getProxy(Class[] argumentTypes, Object[] arguments) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(JedisClusterClient.class);
        enhancer.setCallback(new JedisClusterClientMethodInterceptor());
        return (JedisClusterClient) enhancer.create(argumentTypes, arguments);
    }
}
