package me.gking2224.common.jmx;

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

@Configuration
@Profile("jmx")
public class CommonJmxConfiguration implements ApplicationContextAware {

    private AnnotationJmxAttributeSource annotationJmxAttributeSource;
    
    @SuppressWarnings("unused")
    private ApplicationContext applicationContext;

    public CommonJmxConfiguration() {
        annotationJmxAttributeSource = new AnnotationJmxAttributeSource();
    }
   
    @Bean
    MBeanExporter getMbeanExport() {
        MBeanExporter mbe = new MBeanExporter();
        mbe.setAssembler(getAssembler());
        mbe.setNamingStrategy(getNamingStrategy());
        mbe.setAutodetect(true);
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
