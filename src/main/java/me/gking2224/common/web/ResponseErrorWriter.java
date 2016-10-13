package me.gking2224.common.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.ServletWebRequest;

import me.gking2224.common.client.ErrorResponse;
import me.gking2224.common.utils.ObjectSerializationUtil;

@Component
public class ResponseErrorWriter {

    private static Logger logger = LoggerFactory.getLogger(ResponseErrorWriter.class);
    
    @Autowired
    public ObjectSerializationUtil serializationUtil;
    
    @Autowired
    public ContentNegotiationManager contentNegotiationManager;
    
    public void writeError(
            final HttpServletRequest request, final HttpServletResponse response,
            final HttpStatus status, final String errorMessage)
            throws IOException {
        writeError(request, response, status.value(), errorMessage);
    }
    
    public void writeError(
            final HttpServletRequest request, final HttpServletResponse response, final Throwable t)
            throws IOException {
        Throwable rootCause = ExceptionUtils.getRootCause(t);
        if (rootCause == null) rootCause = t;
        String msg = String.format("%s: %s", rootCause.getClass().getName(), rootCause.getMessage());
        writeError(request, response, new ErrorResponse(500, msg));
    }
    
    public void writeError(
            final HttpServletRequest request, final HttpServletResponse response,
            final int status, final String errorMessage)
                    throws IOException {
        writeError(request, response, new ErrorResponse(status, errorMessage));
    }
    
    public void writeError(final HttpServletRequest request, final HttpServletResponse response, final ErrorResponse e)
            throws IOException {
        MediaType m = null;
        try {
            List<MediaType> mediaTypes = contentNegotiationManager.resolveMediaTypes(new ServletWebRequest(request));
            m = serializationUtil.getSuportedMediaType(mediaTypes);
        }
        catch (Throwable t) {
            logger.info("Could not determine media type, falling back to default (TEXT_PLAIN)");
        }
        if (m == null) {
            m = MediaType.TEXT_PLAIN;
        }
        
        String s = serializationUtil.serializeObject(e, m);
        response.setContentType(m.toString());
        response.setStatus(e.getErrorCode());
        response.getWriter().write(s);
    }
}