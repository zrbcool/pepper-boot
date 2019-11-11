package top.zrbcool.pepper.boot.motan;

import com.pepper.metrics.core.extension.ExtensionLoader;
import com.weibo.api.motan.config.springsupport.BasicRefererConfigBean;
import com.weibo.api.motan.config.springsupport.BasicServiceConfigBean;
import com.weibo.api.motan.config.springsupport.ProtocolConfigBean;
import com.weibo.api.motan.config.springsupport.RegistryConfigBean;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-7
 */
public class BaseMotanConfiguration {

    public static final String PREFIX_APP_MOTAN = "app.motan";
    private static final Set<String> FILTER_SET = new HashSet<>();

    static {
        FILTER_SET.add("pepperProfiler");
        final List<MotanExtRegister> extensions = ExtensionLoader.getExtensionLoader(MotanExtRegister.class).getExtensions();
        for (MotanExtRegister extension : extensions) {
            FILTER_SET.add(extension.name());
        }
    }

    public static void addFilter(String filterName) {
        FILTER_SET.add(filterName);
    }

    protected void initProtocolConfig(ProtocolConfigBean config) {
        config.setName("motan");
        config.setMinWorkerThread(20);
        config.setMaxWorkerThread(200);
        if (CollectionUtils.isNotEmpty(FILTER_SET)) {
            final String temp = StringUtils.arrayToCommaDelimitedString(FILTER_SET.toArray());
            config.setFilter(temp);
        }
//        config.setFilter("cafTracing,pepperProfiler,sentinelProfiler");
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

    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        set.add("a");
        set.add("b");
        set.add("c");

        System.out.println(StringUtils.arrayToCommaDelimitedString(set.toArray()));

        set = new HashSet<>();
        set.add("c");

        System.out.println(StringUtils.arrayToCommaDelimitedString(set.toArray()));

        set = new HashSet<>();

        System.out.println(StringUtils.arrayToCommaDelimitedString(set.toArray()));
    }

}
