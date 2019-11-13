package top.zrbcool.pepper.boot.httpclient;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import top.zrbcool.pepper.boot.core.CustomizedPropertiesBinder;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-11
 */
public class HttpBioClientFactoryBean
        implements FactoryBean<HttpBioClient>, BeanNameAware {
    public static final String PREFIX_APP = "app.httpbioclient";
    private String beanName;

    @Autowired
    protected CustomizedPropertiesBinder binder;

    @Override
    public HttpBioClient getObject() {
        String namespace = StringUtils.substringBefore(beanName, HttpBioClient.class.getSimpleName());

        HttpBioClientProps props = new HttpBioClientProps();
        Bindable<?> target = Bindable.of(HttpBioClientProps.class).withExistingValue(props);
        binder.bind(getPreFix() + "." + namespace + ".props", target);

        HttpBioClient cHttpBioClient = new HttpBioClient(namespace, props);
        cHttpBioClient.init();

        return cHttpBioClient;
    }

    private String getPreFix() {
        return PREFIX_APP;
    }

    @Override
    public Class<?> getObjectType() {
        return HttpBioClient.class;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
}
