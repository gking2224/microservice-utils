package me.gking2224.common.web;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
public class HibernateConfiguration {


    @Value("${jdbc.driverClassName}")
    private String jdbcDriver;

    @Value("${jdbc.url}")
    private String jdbcUrl;
    
    @Value("${jdbc.username}")
    private String jdbcUsername;
    
    @Value("${jdbc.password}")
    private String jdbcPassword;
    
    @Autowired(required=true)
    protected DataSource dataSource;
    
//    @Autowired @Qualifier("hibernateProperties")
//    private Properties myProps;

    @Bean(name="hibernateProperties")
    public Properties getHibernateProperties() throws IOException {
        Properties hibProps = PropertiesLoaderUtils.loadProperties(new ClassPathResource("/hibernate.properties"));
        hibProps.put("connection.driver_class",  this.jdbcDriver);
        hibProps.put("connection.url",  this.jdbcUrl);
        hibProps.put("connection.username",  this.jdbcUsername);
        hibProps.put("connection.password",  this.jdbcPassword);
        
        return hibProps;
    }
    
//    @Bean(name="transactionManager")
//    public PlatformTransactionManager getHibernateTransactionManager() throws Exception {
//        return new HibernateTransactionManager(getHibernateSessionFactory());
//    }
  
//    @Bean
//    public SessionFactory getHibernateSessionFactory() throws Exception {
//        
//        LocalSessionFactoryBean fb = new LocalSessionFactoryBean();
//        fb.setDataSource(dataSource);
//        fb.setHibernateProperties(getHibernateProperties());
//        fb.afterPropertiesSet();
//        return fb.getObject();
//    }
    
}
