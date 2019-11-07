package top.zrbcool.pepper.boot.motan;

import com.weibo.api.motan.config.springsupport.*;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-7
 */
public class BaseMotanConfiguration {

    public static final String PREFIX_APP_MOTAN = "app.motan";

    protected void initProtocolConfig(ProtocolConfigBean config) {
        config.setName("motan");
        config.setMinWorkerThread(20);
        config.setMaxWorkerThread(200);
        config.setFilter("cafTracing,pepperProfiler,sentinelProfiler");
        config.setHaStrategy("failover");
    }

    protected void initRegistryConfig(RegistryConfigBean config) {
        config.setAddress("localhost:2181");
        config.setRegProtocol("zookeeper");
        config.setRequestTimeout(1000);
        config.setConnectTimeout(3000);
    }

    protected void initBasicServiceConfig(RegistryConfigBean defaultRegistry, ProtocolConfigBean defaultProtocol, int port, BasicServiceConfigBean config) {
        config.setRegistry(defaultRegistry);
        config.setProtocol(defaultProtocol);
        config.setRegistry(defaultRegistry.getId());
        config.setCheck(false);
        config.setShareChannel(true);
        config.setRequestTimeout(30000);
        config.setExport(defaultProtocol.getId() + ":" + port);
    }

    protected void initBasicRefererConfig(RegistryConfigBean defaultRegistry, ProtocolConfigBean defaultProtocol, BasicRefererConfigBean config) {
        config.setAccessLog(false);
        config.setCheck(false);
        config.setShareChannel(true);
        config.setProtocol(defaultProtocol);
        config.setProtocol(defaultProtocol.getId());
        config.setRegistry(defaultRegistry);
        config.setRegistry(defaultRegistry.getId());
        config.setRequestTimeout(20000);
    }

}
