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
import me.gking2224.common.utils.PrefixedProperties;

@ComponentScan({ "me.gking2224.common.db" })
@Profile("!embedded")
@Configuration
@EnvironmentProperties(value = "props:/db.properties", prefix = "db", name = "common-db")
public class CommonDatabaseConfiguration {

    @Autowired
    MicroServiceEnvironment env;

    public static final String DRIVER_PROPERTY = "db.database.driver";
    public static final String USERNAME_PROPERTY = "db.database.username";
    public static final String PASSWORD_PROPERTY = "db.database.password";
    public static final String URL_PROPERTY = "db.database.url";

    @Autowired
    @Qualifier("common-db")
    PrefixedProperties dbProperties;

    @Bean(name = "dataSource")
    public DataSource getDataSource() {

        DriverManagerDataSource ds = new DriverManagerDataSource();
        Properties p = dbProperties.unwrap();
        ds.setConnectionProperties(p);
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
    public TransactionTemplate getTransactionTemplate(
            PlatformTransactionManager ptm) {
        TransactionTemplate tt = new TransactionTemplate();
        tt.setTransactionManager(ptm);
        return tt;
    }
}
