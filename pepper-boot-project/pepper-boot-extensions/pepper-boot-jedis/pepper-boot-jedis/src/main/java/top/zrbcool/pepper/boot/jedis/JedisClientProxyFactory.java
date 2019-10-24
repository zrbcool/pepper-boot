package top.zrbcool.pepper.boot.jedis;

import net.sf.cglib.proxy.Enhancer;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-24
 */
public class JedisClientProxyFactory {
    public static JedisClient getProxy(Class[] argumentTypes, Object[] arguments) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(JedisClient.class);
        enhancer.setCallback(new JedisClientMethodInterceptor());
        return (JedisClient) enhancer.create(argumentTypes, arguments);
    }
}
