package me.gking2224.common.web;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.servlet.ViewResolver;

public class ContextInitFinishListener
implements ApplicationListener<ContextRefreshedEvent>{

    Logger logger = LoggerFactory.getLogger(ContextInitFinishListener.class);
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, ViewResolver> beansOfType = event.getApplicationContext().getBeansOfType(ViewResolver.class);
        for (ViewResolver vr : beansOfType.values()) {
            logger.info(""+vr.getClass());
        }
    }
   
}
