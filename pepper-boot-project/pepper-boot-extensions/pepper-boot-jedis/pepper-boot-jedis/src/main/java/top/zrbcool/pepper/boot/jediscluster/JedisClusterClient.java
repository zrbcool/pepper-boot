package top.zrbcool.pepper.boot.jediscluster;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.Set;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-1
 */
public class JedisClusterClient extends JedisCluster {
    private final String namespace;
    private final String address;
    private final GenericObjectPoolConfig poolConfig;

    public JedisClusterClient(Set<HostAndPort> jedisClusterNode, int timeout, int maxAttempts, GenericObjectPoolConfig poolConfig, String namespace, String address) {
        super(jedisClusterNode, timeout, maxAttempts, poolConfig);
        this.namespace = namespace;
        this.address = address;
        this.poolConfig = poolConfig;
    }

//    public JedisClusterClient(String namespace, JedisPoolConfig jedisPoolConfig, String address) {
//        this.namespace = namespace;
//        this.jedisPoolConfig = jedisPoolConfig;
//        this.address = address;
//
//        String[] commonClusterRedisArray = address.split(",");
//        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
//        for (String clusterHostAndPort : commonClusterRedisArray) {
//            String host = clusterHostAndPort.split(":")[0].trim();
//            int port = Integer.parseInt(clusterHostAndPort.split(":")[1].trim());
//            jedisClusterNodes.add(new HostAndPort(host, port));
//        }
//
//        JedisPropsHolder.NAMESPACE.set(namespace);
//        int defaultConnectTimeout = 2000;
//        int defaultConnectMaxAttempts = 20;
//        this.internalJedisCluster = PjedisClusterFactory.newJedisCluster(jedisClusterNodes, defaultConnectTimeout, defaultConnectMaxAttempts, jedisPoolConfig);
//
//    }

    public String getNamespace() {
        return namespace;
    }

    public String getAddress() {
        return address;
    }

    public GenericObjectPoolConfig getPoolConfig() {
        return poolConfig;
    }
}
