package me.gking2224.common.client;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import me.gking2224.common.utils.ObjectSerializationException;
import me.gking2224.common.utils.ObjectSerializationUtil;

@Component
public class ErrorResponseThrowingResponseErrorHandler implements ResponseErrorHandler {
    
    @Autowired
    private ObjectSerializationUtil serializationUtil;
    
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getRawStatusCode() != 200;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        
        final MediaType mediaType = response.getHeaders().getContentType();
        String responseBody = IOUtils.toString(response.getBody(), Charset.defaultCharset());
        try {
            throw new ErrorResponseException(
                    serializationUtil.deserializeToObject(responseBody, mediaType, ErrorResponse.class));
        }
        catch (ObjectSerializationException e) {
            throw new ErrorResponseException(
                    new ErrorResponse(response.getStatusCode(), response.getStatusText()));
        }
    }
}