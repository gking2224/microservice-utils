package me.gking2224.common.jmx;

import javax.management.MBeanServer;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.assembler.MBeanInfoAssembler;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.export.metadata.JmxAttributeSource;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;
import org.springframework.jmx.export.naming.ObjectNamingStrategy;
import org.springframework.jmx.support.MBeanServerFactoryBean;

@Configuration
@Profile("jmx")
public class CommonJmxConfiguration implements ApplicationContextAware {

    public static final String JMX_MBEAN_GROUP_NAME_PREFIX = "MicroService:name=";
    
    private AnnotationJmxAttributeSource annotationJmxAttributeSource;
    
    @SuppressWarnings("unused")
    private ApplicationContext applicationContext;

    public CommonJmxConfiguration() {
        annotationJmxAttributeSource = new AnnotationJmxAttributeSource();
    }

    @Bean("mbeanServer")
    MBeanServerFactoryBean getMbeanServer() {
        MBeanServerFactoryBean mbsfb = new MBeanServerFactoryBean();
        mbsfb.setLocateExistingServerIfPossible(true);
        mbsfb.setRegisterWithFactory(true);
        return mbsfb;
    }
    
    @Bean
    MBeanExporter getMbeanExport(final MBeanServer server) {
        MBeanExporter mbe = new MBeanExporter();
        mbe.setAssembler(getAssembler());
        mbe.setNamingStrategy(getNamingStrategy());
        mbe.setAutodetect(true);
        mbe.setServer(server);
        return mbe;
    }
    

    private MBeanInfoAssembler getAssembler() {
        MetadataMBeanInfoAssembler rv = new MetadataMBeanInfoAssembler();
        rv.setAttributeSource(getAttributeSource());
        return rv;
    }

    private ObjectNamingStrategy getNamingStrategy() {
        MetadataNamingStrategy rv = new org.springframework.jmx.export.naming.MetadataNamingStrategy();
        rv.setAttributeSource(getAttributeSource());
        return rv;
    }

    private JmxAttributeSource getAttributeSource() {
        return annotationJmxAttributeSource;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
