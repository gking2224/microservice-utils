package me.gking2224.common.web;

import java.util.concurrent.TimeUnit;

import org.apache.catalina.Cluster;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Manager;
import org.apache.catalina.ha.ClusterListener;
import org.apache.catalina.ha.ClusterMessage;
import org.apache.catalina.ha.session.DeltaManager;
import org.apache.catalina.ha.tcp.SimpleTcpCluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import me.gking2224.common.client.MicroServiceEnvironment;

@Configuration
@Profile("web")
public class EmbeddedWebApplicationContext {
    protected static final Logger LOG = LoggerFactory.getLogger(EmbeddedWebApplicationContext.class);

    @Bean
    public EmbeddedServletContainerFactory getEmbeddedServletContainerFactory(final MicroServiceEnvironment env) {
        
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.setPort(env.getRequiredProperty("web.httpPort", Integer.class));
        factory.setSessionTimeout(50, TimeUnit.MINUTES);
        factory.setContextPath(env.getRequiredProperty("web.contextPath"));
        factory.addContextCustomizers(tomcatContextCustomizers());
        return factory;
    }

    private TomcatContextCustomizer tomcatContextCustomizers() {
        return new TomcatContextCustomizer() {
            
            @Override
            public void customize(Context context) {
                context.setManager(manager());
                context.setCluster(cluster());
            }
        };
    }

    protected Manager manager() {
        DeltaManager rv = new DeltaManager();
        return rv;
    }

    protected Cluster cluster() {
        SimpleTcpCluster cluster = new SimpleTcpCluster();
        cluster.addClusterListener(clusterListener());
        cluster.addLifecycleListener(lifecycleListener());
        return cluster;
    }

    private LifecycleListener lifecycleListener() {
        return new LifecycleListener() {
            
            @Override
            public void lifecycleEvent(LifecycleEvent event) {
                LOG.debug("Lifecycle event: [type={}, source={}, data={}]",
                        event.getType(), event.getSource(), event.getData());
            }
        };
    }

    private ClusterListener clusterListener() {
        return new ClusterListener() {
            
            
            @Override
            public void messageReceived(ClusterMessage msg) {
                LOG.debug("ClusterListener.messageReceived: [msg={}]", msg);
            }
            
            @Override
            public boolean accept(ClusterMessage msg) {
                LOG.debug("ClusterListener.accept: [msg={}]", msg);
                return true;
            }
        };
    }
    
}
