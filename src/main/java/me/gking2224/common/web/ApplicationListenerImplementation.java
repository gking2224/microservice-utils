package me.gking2224.common.web;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class ApplicationListenerImplementation implements ApplicationListener<ApplicationEvent> {

    /**
     * @param application
     */
    public ApplicationListenerImplementation() {
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        
    }
}