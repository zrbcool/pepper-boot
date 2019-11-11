package top.zrbcool.pepper.boot.mybatis;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.Assert;
import top.zrbcool.pepper.boot.core.AbstractRegistrar;
import top.zrbcool.pepper.boot.util.BeanRegistrationUtil;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-11
 */
public class DataSourceRegistrar extends AbstractRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        final String annotationName = EnableDataSource.class.getName();
        final String annotationsName = EnableDataSources.class.getName();
        registerBeanDefinitions(importingClassMetadata, registry, annotationName, annotationsName);
    }

    protected void dealOne(BeanDefinitionRegistry registry, AnnotationAttributes oneAttributes) {
        String namespace = oneAttributes.getString("namespace");
        Assert.isTrue(StringUtils.isNotEmpty(namespace), "namespace must be specified!");

        String[] mapperPackages = oneAttributes.getStringArray("mapperPackages");
        if (ArrayUtils.isNotEmpty(mapperPackages)) {
            // need scan mappers
            scanAndRegisterMappers(registry, namespace, mapperPackages);
        }

//        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
//                registry, namespace + Log4j2Filter.class.getSimpleName(), Log4j2Filter.class
//        );
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, DataSourceBeanPostProcessor.class.getSimpleName(), DataSourceBeanPostProcessor.class
        );
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + DataSource.class.getSimpleName(), DruidDataSource.class
        );
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + SqlSessionFactory.class.getSimpleName(), SqlSessionFactoryBean.class
        );
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + DataSourceTransactionManager.class.getSimpleName(), DataSourceTransactionManager.class
        );
        BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(
                registry, namespace + StatFilter.class.getSimpleName(), StatFilter.class
        );

    }

    private void scanAndRegisterMappers(BeanDefinitionRegistry registry, String namespace, String[] mapperPackages) {
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }

        Class<? extends Annotation> annotationClass = Annotation.class;
        if (!Annotation.class.equals(annotationClass)) {
            scanner.setAnnotationClass(annotationClass);
        }

        Class<?> markerInterface =  Class.class;
        if (!Class.class.equals(markerInterface)) {
            scanner.setMarkerInterface(markerInterface);
        }

        Class<? extends BeanNameGenerator> generatorClass = BeanNameGenerator.class;
        if (!BeanNameGenerator.class.equals(generatorClass)) {
            scanner.setBeanNameGenerator(BeanUtils.instantiateClass(generatorClass));
        }

        Class<? extends MapperFactoryBean> mapperFactoryBeanClass = MapperFactoryBean.class;
        if (!MapperFactoryBean.class.equals(mapperFactoryBeanClass)) {
            scanner.setMapperFactoryBean(BeanUtils.instantiateClass(mapperFactoryBeanClass));
        }

        scanner.setSqlSessionTemplateBeanName("");
        scanner.setSqlSessionFactoryBeanName(namespace + SqlSessionFactory.class.getSimpleName());

        List<String> basePackages = new ArrayList<String>();
        for (String pkg : mapperPackages) {
            if (StringUtils.isNotEmpty(pkg)) {
                basePackages.add(pkg);
            }
        }
        scanner.registerFilters();

        scanner.doScan(basePackages.toArray(new String[0]));
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
