package me.gking2224.common.utils;

import java.io.IOException;
import java.util.Map;

import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

@Configuration
public class JsonUtil {

    private ObjectMapper om = new ObjectMapper();
    public String mapToJson(Map<String, Object> map) throws JsonProcessingException {
        return om.writeValueAsString(map);
    }
    public String objectToJson(Object o) throws JsonProcessingException {
        return om.writeValueAsString(o);
    }
    
    public String getPathValue(String json, String path) {
        return JsonPath.read(json, path);
    }
    
    public <T> T parseJson(String json, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {
        return om.readValue(json, clazz);
    }

    public Object[] getArray(String json, String path) {
        return ((JSONArray) getValue(json, path)).toArray();
    }

    public Object getFilterValue(String json, String path) {
        Object[] arr = getArray(json, path);
        
        if (arr == null) return null;
        else if (arr.length > 1) throw new IllegalStateException("Expected max one value");
        else return arr[0];
    }

    public Object getFilterValue(String json, String path, int index) {
        Object[] array = getArray(json,  path);
        index = (index < 0) ? array.length - index : index;
        if (array.length < index + 1) return null;
        else return array[index];
    }

    public Object getValue(String json, String path) {
        return JsonPath.parse(json).read(path);
    }

}
