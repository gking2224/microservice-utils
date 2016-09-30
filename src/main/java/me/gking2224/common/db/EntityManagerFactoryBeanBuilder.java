package me.gking2224.common.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

public class EntityManagerFactoryBeanBuilder {

    public static final String DEFAULT_PERSISTENCE_XML_LOCATION = "classpath:META-INF/persistence.xml";
    
    private List<String> packages = new ArrayList<String>();
    private JpaVendorAdapter vendorAdapter;
    private String persistenceXmlLocation = DEFAULT_PERSISTENCE_XML_LOCATION;
    private Properties properties;
    private DataSource dataSource;
    
    public EntityManagerFactoryBeanBuilder packages(String... packages) {
        Stream.of(packages).forEach((s) -> this.packages.add(s));
        return this;
    }

    public EntityManagerFactoryBeanBuilder vendorAdapter(JpaVendorAdapter vendorAdapter) {
        this.vendorAdapter = vendorAdapter;
        return this;
    }

    public EntityManagerFactoryBeanBuilder dataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public EntityManagerFactoryBeanBuilder properties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public EntityManagerFactoryBeanBuilder persistenceXmlLocation(String persistenceXmlLocation) {
        this.persistenceXmlLocation = persistenceXmlLocation;
        return this;
    }

    public LocalContainerEntityManagerFactoryBean build() {
        LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
        emfb.setDataSource(this.dataSource);
        emfb.setPackagesToScan(this.packages.toArray(new String[packages.size()]));
        emfb.setJpaProperties(this.properties);
        emfb.setJpaVendorAdapter(this.vendorAdapter);
        emfb.setPersistenceXmlLocation(this.persistenceXmlLocation);
        return emfb;
    }

}
