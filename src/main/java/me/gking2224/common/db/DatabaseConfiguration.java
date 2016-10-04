package me.gking2224.common.db;

import static me.gking2224.common.utils.SpringConfigurationUtils.getInitProperty;

import java.io.IOException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

//@ManagedResource(
//        log=true,
//        logFile="jmx.log",
//        currencyTimeLimit=15,
//        persistPolicy="OnUpdate",
//        persistPeriod=200,
//        persistLocation="foo",
//        persistName="bar")
public class DatabaseConfiguration {

    public static final String DRIVER_PROPERTY = "jdbc.driverClassName";
    public static final String PORT_PROPERTY = "jdbc.port";
    public static final String USERNAME_PROPERTY = "jdbc.username";
    public static final String PASSWORD_PROPERTY = "jdbc.password";
    public static final String URL_PROPERTY = "jdbc.url";
    public static final String DATABASE_NAME_PROPERTY = "databaseName";
    public static final String AUTOCOMMIT_PROPERTY = "autoCommit";
    public static final String INIT_SQL_PROPERTY = "initSql";
    public static final String DBCP_INITIAL_SIZE_PROPERTY = "dbcpInitialSize";

    @Autowired @Qualifier("dataSourceProperties")
    private Properties dataSourceProperties;

    @Bean(name="dataSourceProperties")
    public Properties getDataSourceProperties() throws IOException {
        Properties dsProps = PropertiesLoaderUtils.loadProperties(new ClassPathResource("/datasource.properties"));
        return dsProps;
    }

    @Bean
    public JdbcTemplate getJdbcTemplate(DataSource dataSource) {
        JdbcTemplate jt = new JdbcTemplate(dataSource);
        return jt;
    }
    
    @Bean(name="dataSource")
    public DataSource getDataSource() {

        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setConnectionProperties(dataSourceProperties);
        ds.setUsername(getInitProperty(dataSourceProperties, USERNAME_PROPERTY));
        ds.setDriverClassName(getInitProperty(dataSourceProperties, DRIVER_PROPERTY));
        ds.setUrl(getInitProperty(dataSourceProperties, URL_PROPERTY));
        ds.setPassword(getInitProperty(dataSourceProperties, PASSWORD_PROPERTY));
        
        return ds;
    }
    
    @Bean(name="transactionManager")
    public PlatformTransactionManager getTransactionManager(
            EntityManagerFactory emf,
            DataSource dataSource,
            JpaDialect jpaDialect
    ) {
        JpaTransactionManager jpatm = new JpaTransactionManager();
        jpatm.setEntityManagerFactory(emf);
        jpatm.setDataSource(dataSource);
        jpatm.setJpaDialect(jpaDialect);
        return jpatm;
    }

    @Bean
    public TransactionTemplate getTransactionTemplate(PlatformTransactionManager ptm) {
        TransactionTemplate tt = new TransactionTemplate();
        tt.setTransactionManager(ptm);
        return tt;
    }
}
