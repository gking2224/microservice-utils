package me.gking2224.common.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import me.gking2224.common.client.ErrorResponseException;
import me.gking2224.common.client.ResponseErrorWriter;

@Component
public class ErrorHandlingFilter extends GenericFilterBean {
    
    @Autowired
    ResponseErrorWriter errorWriter;

    public ErrorHandlingFilter() {
        super();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        try {
            chain.doFilter(request, response);
        }
        catch (ErrorResponseException e) {
            if (!response.isCommitted()) {
                errorWriter.writeError((HttpServletRequest)request, (HttpServletResponse)response, e.getResponse());
            }
            return;
        }
        catch (Throwable t) {
            logger.error(t.getMessage(), t);
            if (!response.isCommitted()) {
                errorWriter.writeError((HttpServletRequest)request, (HttpServletResponse)response, t);
            }
            return;
        }
    }

}
