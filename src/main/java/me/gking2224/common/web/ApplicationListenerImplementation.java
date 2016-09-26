package me.gking2224.common.web;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

final class ApplicationListenerImplementation implements ApplicationListener<ApplicationEvent> {

    /**
     * @param application
     */
    ApplicationListenerImplementation() {
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        
    }
}