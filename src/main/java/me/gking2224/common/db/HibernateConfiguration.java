package me.gking2224.common.db;

import static me.gking2224.common.db.CommonDatabaseConfiguration.DRIVER_PROPERTY;
import static me.gking2224.common.db.CommonDatabaseConfiguration.PASSWORD_PROPERTY;
import static me.gking2224.common.db.CommonDatabaseConfiguration.URL_PROPERTY;
import static me.gking2224.common.db.CommonDatabaseConfiguration.USERNAME_PROPERTY;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import me.gking2224.common.client.EnvironmentProperties;
import me.gking2224.common.client.MicroServiceEnvironment;
import me.gking2224.common.utils.PrefixedProperties;

@Configuration
@EnvironmentProperties(value="props:/hibernate.properties", prefix="hib", name="common-hibernate")
public class HibernateConfiguration {
    
    @Bean(name="hibernateProperties")
    @ConditionalOnMissingBean(name="hibernateProperties")
    public Properties getHibernateProperties(
            @Qualifier("common-hibernate") PrefixedProperties commonHibernateProperties,
            MicroServiceEnvironment env
    ) throws IOException {
        Properties hibProps = new Properties();
        hibProps.putAll(commonHibernateProperties.unwrap());
        hibProps.put("connection.driver_class", env.getRequiredProperty(DRIVER_PROPERTY));
        hibProps.put("connection.url", env.getRequiredProperty(URL_PROPERTY));
        hibProps.put("connection.username", env.getRequiredProperty(USERNAME_PROPERTY));
        hibProps.put("connection.password", env.getRequiredProperty(PASSWORD_PROPERTY));
        
        return hibProps;
    }
    
    @Bean(name="jpaVendorAdapter")
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }
    
    @Bean(name="jpaDialect")
    public JpaDialect jpaDialect() {
        return new HibernateJpaDialect();
    }
}
