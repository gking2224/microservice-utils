package me.gking2224.common.db;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CommonJpaConfiguration {
    
    @Bean(name="entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean getEntityManagerFactory(
            @Qualifier("hibernateProperties") Properties hibernateProperties,
            DataSource dataSource,
            JpaVendorAdapter vendorAdapter,
            @Qualifier("jpaModelBase") String packages
    ) {
        return new EntityManagerFactoryBeanBuilder()
                .packages(packages)
                .properties(hibernateProperties)
                .dataSource(dataSource)
                .vendorAdapter(vendorAdapter)
                .build();
    }
    
    @Bean(name={"jpaTransactionManager", "transactionManager"})
    @Primary
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
}
