package me.gking2224.common.db;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import me.gking2224.common.client.EnvironmentProperties;
import me.gking2224.common.client.MicroServiceEnvironment;

@ComponentScan({"me.gking2224.common.db"})
@Profile("!embedded")
@Configuration
@EnvironmentProperties(value="props:/db.properties", prefix="db", name="common-db")
public class CommonDatabaseConfiguration {
    
    @Autowired MicroServiceEnvironment env;

    public static final String DRIVER_PROPERTY = "db.jdbc.driverClassName";
    public static final String USERNAME_PROPERTY = "db.jdbc.username";
    public static final String PASSWORD_PROPERTY = "db.jdbc.password";
    public static final String URL_PROPERTY = "db.jdbc.url";

    @Autowired @Qualifier("common-db") Properties dbProperties;
    
    @Bean(name="dataSource")
    public DataSource getDataSource() {
        
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setConnectionProperties(dbProperties);
        ds.setUsername(env.getProperty(USERNAME_PROPERTY));
        ds.setDriverClassName(env.getProperty(DRIVER_PROPERTY));
        ds.setUrl(env.getProperty(URL_PROPERTY));
        ds.setPassword(env.getProperty(PASSWORD_PROPERTY));
        return ds;
    }

    @Bean
    public JdbcTemplate getJdbcTemplate(DataSource dataSource) {
        JdbcTemplate jt = new JdbcTemplate(dataSource);
        return jt;
    }

    @Bean
    public TransactionTemplate getTransactionTemplate(PlatformTransactionManager ptm) {
        TransactionTemplate tt = new TransactionTemplate();
        tt.setTransactionManager(ptm);
        return tt;
    }
}
