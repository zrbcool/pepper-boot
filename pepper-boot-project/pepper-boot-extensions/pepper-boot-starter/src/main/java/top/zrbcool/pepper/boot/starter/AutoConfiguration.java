package top.zrbcool.pepper.boot.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import top.zrbcool.pepper.boot.core.CustomizedPropertiesBinder;

/**
 * @author zhangrongbincool@163.com
 * @version 19-9-24
 */
@ConditionalOnClass({
        CustomizedPropertiesBinder.class
})
public class AutoConfiguration {
    @Bean
    public CustomizedPropertiesBinder customizedConfigurationPropertiesBinder() {
        return new CustomizedPropertiesBinder();
    }

}
