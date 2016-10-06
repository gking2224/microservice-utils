package me.gking2224.common.db;

import static me.gking2224.common.db.CommonDatabaseConfiguration.DRIVER_PROPERTY;
import static me.gking2224.common.db.CommonDatabaseConfiguration.PASSWORD_PROPERTY;
import static me.gking2224.common.db.CommonDatabaseConfiguration.URL_PROPERTY;
import static me.gking2224.common.db.CommonDatabaseConfiguration.USERNAME_PROPERTY;
import static me.gking2224.common.utils.PropertyUtils.getString;
import static me.gking2224.common.utils.SpringConfigurationUtils.initPropMissing;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import me.gking2224.common.utils.PropertyUtils;

@Configuration
public class HibernateConfiguration {
    
    @Autowired(required=true)
    protected DataSource dataSource;
    
    @Autowired(required=true) @Qualifier("dataSourceProperties")
    protected Properties dataSourceProperties;
    
    @Bean(name="hibernateProperties")
    public Properties getHibernateProperties() throws IOException {
        Properties hibProps = PropertiesLoaderUtils.loadProperties(new ClassPathResource("/hibernate.properties"));
        hibProps.put("connection.driver_class", getProperty(DRIVER_PROPERTY));
        hibProps.put("connection.url", getProperty(URL_PROPERTY));
        hibProps.put("connection.username", getProperty(USERNAME_PROPERTY));
        hibProps.put("connection.password", getProperty(PASSWORD_PROPERTY));
        
        return hibProps;
    }
    
    @Bean(name="jpaVendorAdapter")
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    private String getProperty(final String property) {
        PropertyUtils.getString(dataSourceProperties, property).orElseThrow(initPropMissing(property));
        return getString(dataSourceProperties, property).orElseThrow(initPropMissing(property));
    }
    
    @Bean(name="hibernateJpaDialect")
    public JpaDialect jpaDialect() {
        return new HibernateJpaDialect();
    }
}
