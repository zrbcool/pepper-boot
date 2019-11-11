package top.zrbcool.pepper.boot.mybatis;

import com.alibaba.druid.filter.logging.Log4j2Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-11
 */
public class BaseDataSourceConfiguration {
    private static String MYBATIS_CONFIG = "mybatis-config.xml";
    private static final Logger logger = LoggerFactory.getLogger(BaseDataSourceConfiguration.class);
    public static final String PREFIX_APP_DATASOURCE = "app.db";

    protected void initStatFilter(StatFilter statFilter) {
        /**
         * https://github.com/alibaba/druid/wiki/%E9%85%8D%E7%BD%AE_StatFilter
         */
        statFilter.setLogSlowSql(true);
        statFilter.setMergeSql(true);
        statFilter.setConnectionStackTraceEnable(false);
        statFilter.setSlowSqlMillis(100);
    }

    protected void initSqlSessionFactoryBean(DataSource dataSource, String typeAliasesPackage, SqlSessionFactoryBean sqlSessionFactoryBean) {
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(new ClassPathResource(MYBATIS_CONFIG));
        if (StringUtils.isNotEmpty(typeAliasesPackage)) {
            sqlSessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage);
        }
    }

    protected void initDataSource(DruidDataSource datasource) {
        /**
         * https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE%E5%B1%9E%E6%80%A7%E5%88%97%E8%A1%A8
         */
        datasource.setDriverClassName("com.mysql.jdbc.Driver");
        datasource.setInitialSize(1);
        datasource.setMinIdle(1);
        datasource.setMaxActive(5);
        datasource.setMaxWait(60000);
        datasource.setTimeBetweenEvictionRunsMillis(60000);
        datasource.setMinEvictableIdleTimeMillis(300000);
        datasource.setTimeBetweenLogStatsMillis(30000);
        datasource.setValidationQuery("SELECT 'x'");
        datasource.setTestWhileIdle(true);
        datasource.setTestOnBorrow(false);
        datasource.setTestOnReturn(false);
        try {
            datasource.setFilters("config,wall");
        } catch (SQLException e) {
            logger.error("druid configuration initialization filter", e);
        }
    }


    protected void initLog4jFilter(Log4j2Filter log4jFilter) {
        log4jFilter.setConnectionLogEnabled(true);
        log4jFilter.setConnectionConnectBeforeLogEnabled(false);
        log4jFilter.setConnectionConnectAfterLogEnabled(false);
        log4jFilter.setConnectionCloseAfterLogEnabled(true);
        log4jFilter.setConnectionCommitAfterLogEnabled(false);
        log4jFilter.setConnectionRollbackAfterLogEnabled(false);
        log4jFilter.setConnectionLogErrorEnabled(true);
        log4jFilter.setDataSourceLogEnabled(false);
        log4jFilter.setStatementLogEnabled(false);
        log4jFilter.setResultSetLogEnabled(false);
    }

}
