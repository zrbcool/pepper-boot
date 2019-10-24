package top.zrbcool.pepper.boot.jedis;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Method;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-24
 */
public class JedisClientMethodInterceptor implements MethodInterceptor {
    private static final Method[] DECLARED_METHODS = JedisClient.class.getDeclaredMethods();

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        for (Method declaredMethod : DECLARED_METHODS) {
            if (declaredMethod == method || declaredMethod.equals(method)) {
                return proxy.invokeSuper(obj, args);
            }
        }

        if (!(obj instanceof JedisClient)) return null;
        final JedisPool jedisPool = ((JedisClient) obj).getJedisPool();

        try (Jedis jedis = jedisPool.getResource()) {
            return method.invoke(jedis, args);
        }
    }
}
