package top.zrbcool.pepper.boot.motan.starter;

import com.weibo.api.motan.config.springsupport.AnnotationBean;
import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-7
 */
@ConditionalOnClass({
        MotanService.class,
        MotanReferer.class,
        AnnotationBean.class
})
public class MotanAutoConfiguration {

    @Bean
    public AnnotationBean annotationBean() {
        return new AnnotationBean();
    }
}
