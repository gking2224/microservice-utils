package me.gking2224.common.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class ContextInitFinishListener
implements ApplicationListener<ContextRefreshedEvent>{

    Logger logger = LoggerFactory.getLogger(ContextInitFinishListener.class);
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
    }
   
}
