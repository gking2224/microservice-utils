package me.gking2224.common;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.security.auth.message.config.AuthConfigFactory;

import org.apache.catalina.authenticator.jaspic.AuthConfigFactoryImpl;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import me.gking2224.common.cli.CommandLineHelper;
import me.gking2224.common.cli.CommandLineHelper.CommandLine;
import me.gking2224.common.client.MicroServiceEnvironment;
import me.gking2224.common.web.ConfigurableEnvironmentImplementation;
import me.gking2224.common.web.ConfigurableWebEnvironmentImplementation;

public abstract class AbstractMicroServiceApplication
implements ApplicationContextInitializer<GenericApplicationContext>{
    static {
        // http://stackoverflow.com/questions/38802437/upgrading-spring-boot-from-1-3-7-to-1-4-0-causing-nullpointerexception-in-authen
        if (AuthConfigFactory.getFactory() == null) {
            AuthConfigFactory.setFactory(new AuthConfigFactoryImpl());
        }
    }
    
    private static final String APP_WEB = "web";

    protected static ConfigurableEnvironment buildEnvironment(CommandLine cl, String appPrefix) {
        MicroServiceEnvironment env = null;
        if (cl.getApps().contains(APP_WEB)) {
            env = new ConfigurableWebEnvironmentImplementation(appPrefix, cl.getEnv(), cl.getApps());
        }
        else env = new ConfigurableEnvironmentImplementation(appPrefix, cl.getEnv(), cl.getApps());
        
        new EnvironmentExtender(env).extendEnvironmentWithAnnotatedProperties("me.gking2224");
        return env;
    }
    
    protected static Class<? extends ConfigurableApplicationContext> getApplicationContextClass(CommandLine cl) {
        if (cl.getApps().contains(APP_WEB)) {
            return AnnotationConfigEmbeddedWebApplicationContext.class;
        }
        else {
            return AnnotationConfigApplicationContext.class;
        }
    }

    @Override
    public void initialize(GenericApplicationContext applicationContext) {
        MicroServiceEnvironment env = (MicroServiceEnvironment)applicationContext.getEnvironment();
//        applicationContext.getBeanFactory().registerSingleton("microserviceEnvironment", env);
        env.registerEnvironmentPropertiesAsBeans(applicationContext);
    }
    
    
    public static class Builder {

        Class<? extends AbstractMicroServiceApplication> applicationClass;
        private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
        private String[] args;
        private String appPrefix;
        
        public Builder(final String[] args) {
            this.args = args;
        }
        
        public Builder applicationClass(Class<? extends AbstractMicroServiceApplication> applicationClass) {
            this.applicationClass = applicationClass;
            return this;
        }
        
        public Builder beanNameGenerator(BeanNameGenerator beanNameGenerator) {
            this.beanNameGenerator = beanNameGenerator;
            return this;
        }
        
        public Builder appPrefix(String appPrefix) {
            this.appPrefix = appPrefix;
            return this;
        }
        
        public SpringApplication build() {
            
            try {
                AbstractMicroServiceApplication appMain = applicationClass.getConstructor().newInstance();
                ;
                CommandLine cl = CommandLineHelper.parseCommandLine(applicationClass.getCanonicalName(), args);
                if (cl == null) return null;
                SpringApplication app = new SpringApplication(applicationClass);
                app.setApplicationContextClass(getApplicationContextClass(cl));
                app.setLogStartupInfo(true);
                app.setWebEnvironment(cl.getApps().contains(APP_WEB));
                app.setBeanNameGenerator(this.beanNameGenerator);
                app.setRegisterShutdownHook(true);
                app.setEnvironment(buildEnvironment(cl, appPrefix));
                app.setInitializers(Arrays.asList(appMain));
                return app;
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
