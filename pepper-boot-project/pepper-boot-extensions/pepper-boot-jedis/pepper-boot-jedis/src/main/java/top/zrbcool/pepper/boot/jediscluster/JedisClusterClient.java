package top.zrbcool.pepper.boot.jediscluster;

import com.pepper.metrics.integration.jedis.PjedisClusterFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPropsHolder;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-1
 */
public class JedisClusterClient extends JedisClusterBridge {

    private final String namespace;
    private final String address;
    private final JedisPoolConfig jedisPoolConfig;
    private final JedisCluster internalJedisCluster;

    public JedisClusterClient(String namespace, JedisPoolConfig jedisPoolConfig, String address) {
        this.namespace = namespace;
        this.jedisPoolConfig = jedisPoolConfig;
        this.address = address;

        String[] commonClusterRedisArray = address.split(",");
        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        for (String clusterHostAndPort : commonClusterRedisArray) {
            String host = clusterHostAndPort.split(":")[0].trim();
            int port = Integer.parseInt(clusterHostAndPort.split(":")[1].trim());
            jedisClusterNodes.add(new HostAndPort(host, port));
        }

        JedisPropsHolder.NAMESPACE.set(namespace);
        int defaultConnectTimeout = 2000;
        int defaultConnectMaxAttempts = 20;
        this.internalJedisCluster = PjedisClusterFactory.newJedisCluster(jedisClusterNodes, defaultConnectTimeout, defaultConnectMaxAttempts, jedisPoolConfig);
    }

    public JedisCluster getInternalJedisCluster() {
        return internalJedisCluster;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getAddress() {
        return address;
    }

    public JedisPoolConfig getJedisPoolConfig() {
        return jedisPoolConfig;
    }
}
