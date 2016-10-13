package me.gking2224.common.db.embedded;

import static me.gking2224.common.utils.SpringConfigurationUtils.getInitProperty;

import java.io.IOException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import me.gking2224.common.db.CommonDatabaseConfiguration;

@ComponentScan({"me.gking2224.common.db"})
public class EmbeddedDatabaseConfiguration implements InitializingBean {
    
    @Autowired(required=false)
    private EmbeddedDatabaseOptions options = new DefaultEmbeddedDatabaseOptions();
    
    @Bean(name="dataSourceProperties")
    public Properties getDataSourceProperties() throws IOException {
        Properties dsProps = PropertiesLoaderUtils.loadProperties(new ClassPathResource("/datasource.properties"));
        dsProps.setProperty(CommonDatabaseConfiguration.USERNAME_PROPERTY, "root");
        dsProps.setProperty(CommonDatabaseConfiguration.PASSWORD_PROPERTY, "");
        return dsProps;
    }
    
    @Bean(name="dataSource")
    public EmbeddedMySQLDatabase getDataSource(
            @Qualifier("dataSourceProperties") Properties dataSourceProperties) {

        EmbeddedMySQLDatabaseBuilder builder = new EmbeddedMySQLDatabaseBuilder()
            .setPort(Integer.parseInt(getInitProperty(dataSourceProperties, CommonDatabaseConfiguration.PORT_PROPERTY)))
            .addScripts(options.getScripts())
            .options(options.getDatabaseOptions())
            .sessionVariables(options.getSessionVariables())
            .setDatabaseName(getInitProperty(dataSourceProperties, CommonDatabaseConfiguration.DATABASE_NAME_PROPERTY));
        
        return builder.build();
    }

    @Bean
    public JdbcTemplate getJdbcTemplate(DataSource dataSource) {
        JdbcTemplate jt = new JdbcTemplate(dataSource);
        return jt;
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

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
