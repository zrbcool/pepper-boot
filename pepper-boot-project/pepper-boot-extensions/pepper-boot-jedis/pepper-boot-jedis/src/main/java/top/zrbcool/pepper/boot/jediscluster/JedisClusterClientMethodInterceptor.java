package top.zrbcool.pepper.boot.jediscluster;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.PjedisPool;
import top.zrbcool.pepper.boot.jedis.JedisClient;

import java.lang.reflect.Method;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-24
 */
public class JedisClusterClientMethodInterceptor implements MethodInterceptor {
    private static final Method[] DECLARED_METHODS = JedisClusterClient.class.getDeclaredMethods();

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        for (Method declaredMethod : DECLARED_METHODS) {
            if (declaredMethod == method || declaredMethod.equals(method)) {
                return proxy.invokeSuper(obj, args);
            }
        }

        if (!(obj instanceof JedisClusterClient)) return null;
        final JedisCluster jedisCluster = ((JedisClusterClient) obj).getInternalJedisCluster();

        return method.invoke(jedisCluster, args);
    }
}
