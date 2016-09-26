package me.gking2224.common.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class AccessFilter extends GenericFilterBean {

    private boolean block = false;
    
    private static Logger logger = LoggerFactory.getLogger(AccessFilter.class);
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (block) {
            logger.warn("Blocking access");
        }
        else {
            chain.doFilter(request, response);
        }
        
    }

}
