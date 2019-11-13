package top.zrbcool.pepper.boot.asynchttpclient;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.nio.reactor.IOReactorException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import top.zrbcool.pepper.boot.core.CustomizedPropertiesBinder;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-11
 */
public class HttpClientFactoryBean
        implements FactoryBean<HttpClient>, BeanNameAware {
    public static final String PREFIX_APP_JEDIS = "app.chttpclient";
    private String beanName;

    @Autowired
    protected CustomizedPropertiesBinder binder;

    @Override
    public HttpClient getObject() throws IOReactorException {
        String namespace = StringUtils.substringBefore(beanName, HttpClient.class.getSimpleName());

        HttpClientProps props = new HttpClientProps();
        Bindable<?> target = Bindable.of(HttpClientProps.class).withExistingValue(props);
        binder.bind(getPreFix() + "." + namespace + ".props", target);

        HttpClient cHttpClient = new HttpClient(namespace, props);
        cHttpClient.init();


        return cHttpClient;
    }

    private String getPreFix() {
        return PREFIX_APP_JEDIS;
    }

    @Override
    public Class<?> getObjectType() {
        return HttpClient.class;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
}
